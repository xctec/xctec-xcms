package com.df4j.xctec.xcms.base.security;

import com.df4j.xctec.xcms.core.security.AuthPrincipal;
import com.df4j.xctec.xcms.core.security.TokenPair;
import com.df4j.xctec.xcms.core.security.TokenStore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 内存 Token 存储默认实现。
 *
 * 三索引保证按用户 / 按租户批量踢线 O(1) 定位：
 * - tokenIndex: accessToken -> TokenEntry
 * - refreshIndex: refreshToken -> TokenEntry
 * - userIndex: userId -> Set<accessToken>
 * - tenantIndex: tenantId -> Set<accessToken>
 *
 * 单实例部署，接受重启失效。多服务场景下替换为 Redis / JWT 实现。
 */
@Component
@ConditionalOnMissingBean(TokenStore.class)
public class MemoryTokenStore implements TokenStore {

    private static final long ACCESS_EXPIRES_SECONDS = 7200L;       // 2 小时
    private static final long REFRESH_EXPIRES_SECONDS = 604800L;    // 7 天

    private final Map<String, TokenEntry> tokenIndex = new ConcurrentHashMap<>();
    private final Map<String, TokenEntry> refreshIndex = new ConcurrentHashMap<>();
    private final Map<Long, Set<String>> userIndex = new ConcurrentHashMap<>();
    private final Map<Long, Set<String>> tenantIndex = new ConcurrentHashMap<>();
    private final SecureRandom random = new SecureRandom();

    @Override
    public TokenPair issue(AuthPrincipal principal) {
        String accessToken = generateToken();
        String refreshToken = generateToken();
        long now = Instant.now().getEpochSecond();

        TokenEntry entry = new TokenEntry(accessToken, refreshToken, principal,
                now + ACCESS_EXPIRES_SECONDS, now + REFRESH_EXPIRES_SECONDS);
        tokenIndex.put(accessToken, entry);
        refreshIndex.put(refreshToken, entry);
        userIndex.computeIfAbsent(principal.userId(), k -> ConcurrentHashMap.newKeySet()).add(accessToken);
        tenantIndex.computeIfAbsent(principal.tenantId(), k -> ConcurrentHashMap.newKeySet()).add(accessToken);

        return new TokenPair(accessToken, refreshToken, ACCESS_EXPIRES_SECONDS, REFRESH_EXPIRES_SECONDS);
    }

    @Override
    public Optional<AuthPrincipal> verify(String accessToken) {
        if (accessToken == null) {
            return Optional.empty();
        }
        TokenEntry entry = tokenIndex.get(accessToken);
        if (entry == null || entry.isAccessExpired()) {
            if (entry != null) {
                evict(entry);
            }
            return Optional.empty();
        }
        return Optional.of(entry.principal);
    }

    @Override
    public TokenPair refresh(String refreshToken) {
        TokenEntry entry = refreshIndex.get(refreshToken);
        if (entry == null || entry.isRefreshExpired()) {
            if (entry != null) {
                evict(entry);
            }
            throw new com.df4j.xctec.xcms.core.exception.UnauthorizedException(
                    com.df4j.xctec.xcms.core.result.CommonErrorCode.SYSTEM_ERROR, "refresh token expired");
        }
        evict(entry);
        return issue(entry.principal);
    }

    @Override
    public void revoke(String accessToken) {
        TokenEntry entry = tokenIndex.remove(accessToken);
        if (entry != null) {
            refreshIndex.remove(entry.refreshToken);
            removeFromIndex(userIndex, entry.principal.userId(), accessToken);
            removeFromIndex(tenantIndex, entry.principal.tenantId(), accessToken);
        }
    }

    @Override
    public void revokeByUser(Long userId) {
        Set<String> tokens = userIndex.remove(userId);
        if (tokens != null) {
            new HashSet<>(tokens).forEach(this::revoke);
        }
    }

    @Override
    public void revokeByTenant(Long tenantId) {
        Set<String> tokens = tenantIndex.remove(tenantId);
        if (tokens != null) {
            new HashSet<>(tokens).forEach(this::revoke);
        }
    }

    private void evict(TokenEntry entry) {
        tokenIndex.remove(entry.accessToken);
        refreshIndex.remove(entry.refreshToken);
        removeFromIndex(userIndex, entry.principal.userId(), entry.accessToken);
        removeFromIndex(tenantIndex, entry.principal.tenantId(), entry.accessToken);
    }

    private void removeFromIndex(Map<Long, Set<String>> index, Long key, String token) {
        Set<String> set = index.get(key);
        if (set != null) {
            set.remove(token);
            if (set.isEmpty()) {
                index.remove(key);
            }
        }
    }

    private String generateToken() {
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        StringBuilder sb = new StringBuilder(64);
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    private static class TokenEntry {
        final String accessToken;
        final String refreshToken;
        final AuthPrincipal principal;
        final long accessExpiresAt;
        final long refreshExpiresAt;

        TokenEntry(String accessToken, String refreshToken, AuthPrincipal principal,
                   long accessExpiresAt, long refreshExpiresAt) {
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
            this.principal = principal;
            this.accessExpiresAt = accessExpiresAt;
            this.refreshExpiresAt = refreshExpiresAt;
        }

        boolean isAccessExpired() {
            return Instant.now().getEpochSecond() > accessExpiresAt;
        }

        boolean isRefreshExpired() {
            return Instant.now().getEpochSecond() > refreshExpiresAt;
        }
    }
}
