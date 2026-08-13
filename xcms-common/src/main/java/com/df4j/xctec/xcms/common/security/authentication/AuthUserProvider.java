package com.df4j.xctec.xcms.common.security.authentication;

import java.util.Optional;

/**
 * 认证用户数据提供者端口（六边形架构的 inbound 端口）。
 * <p>
 * 认证模块（xcms-auth）仅面向此抽象取数，不感知具体数据来源（JPA / 远程服务 / LDAP 等）。
 * 由持有用户数据的模块（如 xcms-system-impl）提供实现，从而解耦 auth 与 system 两业务模块。
 * </p>
 */
public interface AuthUserProvider {

    /**
     * 按租户与用户名查询认证用户。
     *
     * @param tenantId 租户ID，来自 {@code TenantContext}
     * @param username 用户名/账号
     * @return 命中则 {@link Optional#isPresent()} 为 true，否则为空
     */
    Optional<AuthUser> loadByTenantAndUsername(Long tenantId, String username);
}
