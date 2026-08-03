package com.df4j.xctec.xcms.core.security;

import java.util.Optional;

/**
 * 令牌存储抽象。支持 access / refresh 两种令牌的读写与吊销。
 * 接口定义于 xcms-core，当前默认实现在 xcms-base（MemoryTokenStore），
 * 多服务场景下将提供 Redis / JWT 等集中式实现，接口契约不变。
 */
public interface TokenStore {

    /**
     * 签发令牌对。
     */
    TokenPair issue(AuthPrincipal principal);

    /**
     * 校验 accessToken，返回认证主体。
     */
    Optional<AuthPrincipal> verify(String accessToken);

    /**
     * 刷新令牌。
     */
    TokenPair refresh(String refreshToken);

    /**
     * 吊销指定 accessToken。
     */
    void revoke(String accessToken);

    /**
     * 吊销指定用户的所有令牌（踢线）。
     */
    void revokeByUser(Long userId);

    /**
     * 吊销指定租户的所有令牌（租户停用即时踢线）。
     */
    void revokeByTenant(Long tenantId);
}
