package com.df4j.xctec.xcms.core.security;

import java.io.Serializable;
import java.util.Set;

/**
 * 认证主体：解析令牌后放入 TenantContext / SecurityContext。
 */
public record AuthPrincipal(
        Long userId,
        String username,
        Long tenantId,
        String tenantPath,
        String tenantCodePath,
        Set<String> permissions,
        Set<Long> roleIds
) implements Serializable {

    public boolean hasPermission(String permission) {
        return permissions != null && permissions.contains(permission);
    }
}
