package com.df4j.xctec.xcms.common.tenant;

import com.alibaba.ttl.TransmittableThreadLocal;

/**
 * 租户上下文（基于 TTL，跨线程透传）。
 * 持有当前请求的租户（业务隔离用）与认证主体（管理域用）。
 */
public final class TenantContext {

    private static final TransmittableThreadLocal<TenantContext> CTX = new TransmittableThreadLocal<>();

    private Long tenantId;
    private AuthPrincipalHolder principal;
    private boolean elevated;

    private TenantContext() {
    }

    public static TenantContext current() {
        TenantContext c = CTX.get();
        if (c == null) {
            c = new TenantContext();
            CTX.set(c);
        }
        return c;
    }

    public static void set(TenantContext ctx) {
        CTX.set(ctx);
    }

    public static void clear() {
        CTX.remove();
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public AuthPrincipalHolder getPrincipal() {
        return principal;
    }

    public void setPrincipal(AuthPrincipalHolder principal) {
        this.principal = principal;
    }

    public boolean isElevated() {
        return elevated;
    }

    public void setElevated(boolean elevated) {
        this.elevated = elevated;
    }

    /** 轻量认证主体引用，避免与 security 包循环依赖 */
    public static class AuthPrincipalHolder {
        private Long userId;
        private Long tenantId;
        private Long effectiveTenantId;

        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
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
    }
}
