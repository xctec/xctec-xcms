package com.df4j.xctec.xcms.common.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import org.hibernate.annotations.TenantId;

/**
 * 业务数据实体基类：带 @TenantId 实现按租户精确等值隔离。
 * 注意：租户实体（sys_tenant）不要继承此类，否则会摧毁级联下钻。
 */
@MappedSuperclass
public abstract class TenantAwareEntity extends BaseEntity {

    @TenantId
    @Column(name = "tenant_id", nullable = false, updatable = false)
    protected Long tenantId;

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }
}
