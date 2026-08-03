package com.df4j.xctec.xcms.base.tenant;

import com.df4j.xctec.xcms.core.tenant.TenantContext;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;

/**
 * Hibernate 多租户标识符解析器：返回当前业务租户 id。
 * 业务数据按 tenant_id 精确等值隔离，故直接取 TenantContext 的 tenantId。
 *
 * 此类为无状态类，仅依赖 TenantContext 静态方法，可由 Hibernate 直接实例化。
 * 配置方式：spring.jpa.properties.hibernate.multi-tenancy.identifier-resolver=com.df4j.xctec.xcms.base.tenant.TenantIdentifierResolver
 */
public class TenantIdentifierResolver implements CurrentTenantIdentifierResolver<Long> {

    /** 平台默认租户 / 初始化场景使用 */
    public static final Long SYSTEM_TENANT_ID = 0L;

    @Override
    public Long resolveCurrentTenantIdentifier() {
        return TenantContext.currentTenantId().orElse(SYSTEM_TENANT_ID);
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return true;
    }
}
