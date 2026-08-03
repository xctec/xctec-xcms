package com.df4j.xctec.xcms.core.tenant;

/**
 * 管理域越权校验。回答"当前主体能否操作目标租户"。
 * 规则：目标必须是自身或子孙（级联下钻），否则拒绝。
 * 默认实现在 xcms-base（DefaultTenantGuard），可被业务模块覆盖。
 */
public interface TenantGuard {

    /**
     * 校验当前主体能否操作目标租户。
     *
     * @param targetTenantId 目标租户 id
     * @throws com.df4j.xctec.xcms.core.exception.ForbiddenException    无权管理
     * @throws com.df4j.xctec.xcms.core.exception.UnauthorizedException 未认证
     */
    void assertManageable(Long targetTenantId);

    /**
     * 返回当前主体可管理的子孙 path 前缀（含自身）。
     * 用于下钻查询：tenant.treePath.path.startsWith(prefix)。
     */
    String manageableScopePrefix();
}
