package com.df4j.xctec.xcms.common.tenant;

import org.hibernate.context.spi.CurrentTenantIdentifierResolver;

/**
 * Hibernate 多租户标识符解析器：返回当前业务租户 id。
 * 业务数据按 tenant_id 精确等值隔离，故直接取 TenantContext 的 tenantId。
 */
public class TenantIdentifierResolver implements CurrentTenantIdentifierResolver<Long> {

    @Override
    public Long resolveCurrentTenantIdentifier() {
        Long id = TenantContext.current().getTenantId();
        if (id == null) {
            // 平台默认租户或初始化场景
            return 0L;
        }
        return id;
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return true;
    }
}
