package com.df4j.xctec.xcms.core.context.tenant;

public class ThreadLocalTenantContextAccessor implements TenantContextAccessor{
    private final ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    @Override
    public void set(Long tenantId) {
        threadLocal.set(tenantId);
    }

    @Override
    public void clear() {
        threadLocal.remove();
    }

    @Override
    public Long getOrNull() {
        return threadLocal.get();
    }
}
