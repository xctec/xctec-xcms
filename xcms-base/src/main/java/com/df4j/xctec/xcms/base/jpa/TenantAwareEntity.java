package com.df4j.xctec.xcms.base.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.TenantId;

/**
 * 业务数据实体基类：带 @TenantId 实现按租户精确等值隔离。
 * Hibernate 自动在所有查询与写入上追加 tenant_id = ?，业务代码无感知且无法遗漏。
 *
 * 注意：租户实体（sys_tenant）不要继承此类，否则会摧毁级联下钻。
 * Tenant / Permission / Dict 等平台级元数据应直接继承 BaseEntity。
 */
@MappedSuperclass
@Getter
@Setter
public abstract class TenantAwareEntity extends BaseEntity {

    @TenantId
    @Column(name = "tenant_id", nullable = false, updatable = false)
    private Long tenantId;
}
