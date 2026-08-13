package com.df4j.xctec.xcms.core.context.tenant;

import java.util.Optional;

public class TenantContextUtils {
    private static TenantContextAccessor instance;

    public static void bind(TenantContextAccessor tenantContextAccessor) {
        instance = tenantContextAccessor;
    }

    private static TenantContextAccessor current() {
        TenantContextAccessor accessor = instance;
        if (accessor == null) {
            throw new IllegalStateException("TenantContextAccessor 尚未初始化，请确认已经注入");
        }
        return accessor;
    }

    public static void setTenantId(Long tenantId) {
        current().set(tenantId);
    }

    public static void clear() {
        current().clear();
    }

    public static Long requireTenantId() {
        return current().get();
    }

    public static Optional<Long> tenantId() {
        return Optional.ofNullable(current().getOrNull());
    }
}
