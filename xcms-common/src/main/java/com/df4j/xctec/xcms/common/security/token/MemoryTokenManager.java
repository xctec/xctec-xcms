package com.df4j.xctec.xcms.common.security.token;

import com.df4j.xctec.xcms.core.vo.TokenVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.SecureRandom;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 基于内存的 TokenManager 实现。
 * <p>
 * 采用不透明（opaque）token 方案：登录时为认证信息随机生成 accessToken / refreshToken 字符串，
 * 并将关联关系保存在内存 Map 中。parse 时通过 token 字符串反查 Authentication。
 * 适用于单体、非集群场景；集群环境请改用 Redis 等共享存储实现。
 * <p>
 * 会话绑定：每次签发会生成唯一的 {@code sessionId}，accessToken 与 refreshToken 互相持有对方的引用，
 * 使得「刷新吊销旧 access」「登出/踢人按会话整体失效」能够闭环（同一会话的两个令牌同时被清除）。
 *
 * @author Ray.Hao
 * @since 0.0.1
 */
public class MemoryTokenManager extends AbstractTokenManager {

    private static final Logger logger = LoggerFactory.getLogger(MemoryTokenManager.class);

    private static final long DEFAULT_ACCESS_TTL_MILLIS = 30 * 60 * 1000L;
    private static final long DEFAULT_REFRESH_TTL_MILLIS = 7 * 24 * 60 * 60 * 1000L;

    private static final char[] HEX = "0123456789abcdef".toCharArray();

    private final SecureRandom secureRandom = new SecureRandom();

    private final long accessTokenTtlMillis;
    private final long refreshTokenTtlMillis;

    /**
     * accessToken -> 会话令牌条目（含关联的 refreshToken 引用）
     */
    private final Map<String, SessionTokenEntry> accessTokens = new ConcurrentHashMap<>();
    /**
     * refreshToken -> 会话令牌条目（含关联的 accessToken 引用）
     */
    private final Map<String, SessionTokenEntry> refreshTokens = new ConcurrentHashMap<>();

    public MemoryTokenManager() {
        this(DEFAULT_ACCESS_TTL_MILLIS, DEFAULT_REFRESH_TTL_MILLIS);
    }

    public MemoryTokenManager(long accessTokenTtlMillis, long refreshTokenTtlMillis) {
        if (accessTokenTtlMillis <= 0 || refreshTokenTtlMillis <= 0) {
            throw new IllegalArgumentException("token 过期时间必须大于 0");
        }
        this.accessTokenTtlMillis = accessTokenTtlMillis;
        this.refreshTokenTtlMillis = refreshTokenTtlMillis;
    }

    @Override
    public TokenVo generate(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new InsufficientAuthenticationException("认证信息无效，无法生成 token");
        }
        long now = System.currentTimeMillis();
        Long userId = extractUserId(authentication);
        String username = authentication.getName();

        // 同一会话：access 与 refresh 共享 sessionId 并互相引用
        String sessionId = generateSessionId();
        String accessToken = generateToken();
        String refreshToken = generateToken();

        accessTokens.put(accessToken, new SessionTokenEntry(sessionId, accessToken, refreshToken,
                authentication, now + accessTokenTtlMillis, userId, username));
        refreshTokens.put(refreshToken, new SessionTokenEntry(sessionId, accessToken, refreshToken,
                authentication, now + refreshTokenTtlMillis, userId, username));

        logger.debug("生成 token 成功, sessionId: {}, userId: {}, username: {}", sessionId, userId, username);
        return TokenVo.builder()
                .tokenType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn((int) (accessTokenTtlMillis / 1000))
                .build();
    }

    @Override
    public boolean validate(String token) {
        if (token == null) {
            return false;
        }
        SessionTokenEntry entry = accessTokens.get(token);
        if (entry == null) {
            return false;
        }
        if (entry.isExpired()) {
            accessTokens.remove(token);
            return false;
        }
        return true;
    }

    @Override
    public Authentication parse(String token) {
        if (token == null) {
            throw new InsufficientAuthenticationException("token 不能为空");
        }
        SessionTokenEntry entry = accessTokens.get(token);
        if (entry == null) {
            throw new InsufficientAuthenticationException("token 不存在");
        }
        if (entry.isExpired()) {
            accessTokens.remove(token);
            throw new InsufficientAuthenticationException("token 已过期");
        }
        return entry.getAuthentication();
    }

    @Override
    public boolean validateRefreshToken(String refreshToken) {
        if (refreshToken == null) {
            return false;
        }
        SessionTokenEntry entry = refreshTokens.get(refreshToken);
        if (entry == null) {
            return false;
        }
        if (entry.isExpired()) {
            refreshTokens.remove(refreshToken);
            return false;
        }
        return true;
    }

    @Override
    public TokenVo refreshToken(String refreshToken) {
        if (refreshToken == null) {
            throw new InsufficientAuthenticationException("refreshToken 不能为空");
        }
        SessionTokenEntry entry = refreshTokens.get(refreshToken);
        if (entry == null) {
            throw new InsufficientAuthenticationException("refreshToken 不存在");
        }
        if (entry.isExpired()) {
            refreshTokens.remove(refreshToken);
            throw new InsufficientAuthenticationException("refreshToken 已过期");
        }
        // 一次性使用：先整体吊销旧会话（旧 refresh + 旧 access 同时失效），再签发全新一对令牌
        revokeSessionByRefresh(refreshToken);
        return generate(entry.getAuthentication());
    }

    @Override
    public void invalid(String token) {
        if (token == null) {
            return;
        }
        // token 可能是 access 或 refresh，按会话整体失效：移除自身及其配对令牌
        SessionTokenEntry accessEntry = accessTokens.get(token);
        if (accessEntry != null) {
            accessTokens.remove(token);
            refreshTokens.remove(accessEntry.getRefreshToken());
            logger.debug("注销会话成功, sessionId: {}", accessEntry.getSessionId());
            return;
        }
        SessionTokenEntry refreshEntry = refreshTokens.get(token);
        if (refreshEntry != null) {
            refreshTokens.remove(token);
            accessTokens.remove(refreshEntry.getAccessToken());
            logger.debug("注销会话成功, sessionId: {}", refreshEntry.getSessionId());
        }
    }

    @Override
    public void kickSession(String token) {
        // 与 invalid 一致：按会话整体失效（覆盖 access / refresh 两种入参）
        invalid(token);
    }

    @Override
    public void kickUserSessions(Long userId) {
        if (userId == null) {
            return;
        }
        accessTokens.entrySet().removeIf(e -> userId.equals(e.getValue().getUserId()));
        refreshTokens.entrySet().removeIf(e -> userId.equals(e.getValue().getUserId()));
        logger.debug("踢掉用户所有会话, userId: {}", userId);
    }

    /**
     * 清理所有已过期的 token（access / refresh），定时调用以释放内存。
     * <p>
     * 不透明令牌一旦过期且不再被访问便长期驻留内存（无 TTL 自动回收），
     * 故以固定间隔扫描并移除，避免随用户量增长造成内存泄漏。
     */
    @Scheduled(fixedDelay = 5 * 60 * 1000L)
    public void cleanupExpired() {
        long now = System.currentTimeMillis();
        accessTokens.entrySet().removeIf(e -> e.getValue().getExpireAt() <= now);
        refreshTokens.entrySet().removeIf(e -> e.getValue().getExpireAt() <= now);
    }

    /**
     * 整体吊销某个 refreshToken 所属会话：移除旧 refresh 与其关联的旧 access。
     */
    private void revokeSessionByRefresh(String refreshToken) {
        SessionTokenEntry entry = refreshTokens.remove(refreshToken);
        if (entry != null) {
            accessTokens.remove(entry.getAccessToken());
            logger.debug("刷新时吊销旧会话, sessionId: {}", entry.getSessionId());
        }
    }

    private String generateToken() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        char[] chars = new char[bytes.length * 2];
        for (int i = 0; i < bytes.length; i++) {
            int v = bytes[i] & 0xFF;
            chars[i * 2] = HEX[v >>> 4];
            chars[i * 2 + 1] = HEX[v & 0x0F];
        }
        return new String(chars);
    }

    private String generateSessionId() {
        byte[] bytes = new byte[16];
        secureRandom.nextBytes(bytes);
        char[] chars = new char[bytes.length * 2];
        for (int i = 0; i < bytes.length; i++) {
            int v = bytes[i] & 0xFF;
            chars[i * 2] = HEX[v >>> 4];
            chars[i * 2 + 1] = HEX[v & 0x0F];
        }
        return new String(chars);
    }

    @Nullable
    private static Long extractUserId(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (principal == null) {
            return null;
        }
        if (principal instanceof Number) {
            return ((Number) principal).longValue();
        }
        try {
            Method method = principal.getClass().getMethod("getUserId");
            Object result = method.invoke(principal);
            if (result instanceof Number) {
                return ((Number) result).longValue();
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ignored) {
            // 非标准 principal，忽略，按 username 维度管理
        }
        return null;
    }

    /**
     * 会话令牌条目：同一会话的 access / refresh 共用 sessionId 并互相持有引用，
     * 从而支持「刷新吊销旧 access」「登出/踢人按会话整体失效」。
     */
    private static final class SessionTokenEntry {
        private final String sessionId;
        private final String accessToken;
        private final String refreshToken;
        private final Authentication authentication;
        private final long expireAt;
        private final Long userId;
        private final String username;

        private SessionTokenEntry(String sessionId, String accessToken, String refreshToken,
                                  Authentication authentication, long expireAt, Long userId, String username) {
            this.sessionId = sessionId;
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
            this.authentication = authentication;
            this.expireAt = expireAt;
            this.userId = userId;
            this.username = username;
        }

        private String getSessionId() {
            return sessionId;
        }

        private String getAccessToken() {
            return accessToken;
        }

        private String getRefreshToken() {
            return refreshToken;
        }

        private Authentication getAuthentication() {
            return authentication;
        }

        private long getExpireAt() {
            return expireAt;
        }

        private Long getUserId() {
            return userId;
        }

        @SuppressWarnings("unused")
        private String getUsername() {
            return username;
        }

        private boolean isExpired() {
            return System.currentTimeMillis() > expireAt;
        }
    }
}
