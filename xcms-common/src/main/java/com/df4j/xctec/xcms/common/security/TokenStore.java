package com.df4j.xctec.xcms.common.security;

import java.util.Optional;

/**
 * 令牌存储抽象。支持 access / refresh 两种令牌的读写与吊销。
 * 默认提供内存实现；生产可替换为 Redis 实现。
 */
public interface TokenStore {

    void saveAccess(String accessToken, AuthPrincipal principal, java.time.Duration ttl);

    void saveRefresh(String refreshToken, AuthPrincipal principal, java.time.Duration ttl);

    Optional<AuthPrincipal> getByAccess(String accessToken);

    Optional<AuthPrincipal> getByRefresh(String refreshToken);

    void revokeAccess(String accessToken);

    void revokeRefresh(String refreshToken);

    boolean accessExists(String accessToken);
}
