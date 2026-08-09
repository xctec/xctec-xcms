package com.df4j.xctec.xcms.core.context;

import java.util.Optional;

public class TenantContextUtils {
    private static ContextAccessor<Long> instance;

    public static void bind(ContextAccessor<Long> tenantContextAccessor) {
        instance = tenantContextAccessor;
    }

    private static ContextAccessor<Long> current() {
        ContextAccessor<Long> accessor = instance;
        if (accessor == null) {
            throw new IllegalStateException("ContextAccessor 尚未初始化，请确认已经注入");
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
        return Optional.of(current().get());
    }
}
