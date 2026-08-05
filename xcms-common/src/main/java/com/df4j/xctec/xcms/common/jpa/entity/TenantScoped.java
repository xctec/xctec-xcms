package com.df4j.xctec.xcms.common.jpa.entity;

public interface TenantScoped {

    void setTenantId(Long tenantId);

    Long getTenantId();
}
