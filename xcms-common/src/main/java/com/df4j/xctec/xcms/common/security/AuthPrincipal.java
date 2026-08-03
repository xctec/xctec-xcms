package com.df4j.xctec.xcms.common.security;

import java.io.Serializable;
import java.util.Set;

/**
 * 认证主体：解析令牌后放入 TenantContext / SecurityContext。
 */
public class AuthPrincipal implements Serializable {

    private Long userId;
    private String username;
    private Long tenantId;
    private Long effectiveTenantId;
    private Set<String> roles;
    private Set<String> permissions;

    public AuthPrincipal() {
    }

    public AuthPrincipal(Long userId, String username, Long tenantId,
                         Long effectiveTenantId, Set<String> roles, Set<String> permissions) {
        this.userId = userId;
        this.username = username;
        this.tenantId = tenantId;
        this.effectiveTenantId = effectiveTenantId;
        this.roles = roles;
        this.permissions = permissions;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Long getEffectiveTenantId() {
        return effectiveTenantId != null ? effectiveTenantId : tenantId;
    }

    public void setEffectiveTenantId(Long effectiveTenantId) {
        this.effectiveTenantId = effectiveTenantId;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public Set<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<String> permissions) {
        this.permissions = permissions;
    }

    public boolean hasPermission(String permission) {
        return permissions != null && permissions.contains(permission);
    }
}
