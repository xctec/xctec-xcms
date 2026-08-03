package com.df4j.xctec.xcms.core.tenant;

import java.io.Serializable;

/**
 * 租户信息（管理域）。注意：Tenant 实体不带 @TenantId，否则摧毁级联下钻。
 * 此 DTO 用于上下文与缓存，不含 JPA 注解。
 */
public record TenantInfo(
        Long tenantId,
        String tenantCode,
        String name,
        String path,
        String codePath,
        Integer level
) implements Serializable {

    /**
     * 子孙 path 前缀。
     * path 不含自身 id，故下钻前缀 = path + tenantId + "/"。
     */
    public String scopePrefix() {
        return path + tenantId + "/";
    }
}
