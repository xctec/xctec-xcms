package com.df4j.xctec.xcms.core.tenant;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.df4j.xctec.xcms.core.exception.BizException;
import com.df4j.xctec.xcms.core.result.CommonErrorCode;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * 租户上下文（基于 TTL，跨线程透传）。
 * 不暴露裸 set()，强制走 runWith，从 API 层面杜绝线程池上下文污染。
 */
public final class TenantContext {

    private static final ThreadLocal<TenantInfo> HOLDER = new TransmittableThreadLocal<>();

    private TenantContext() {
    }

    public static Optional<TenantInfo> current() {
        return Optional.ofNullable(HOLDER.get());
    }

    public static Optional<Long> currentTenantId() {
        return current().map(TenantInfo::tenantId);
    }

    public static Long requireTenantId() {
        return currentTenantId().orElseThrow(
                () -> new BizException(CommonErrorCode.TENANT_CONTEXT_MISSING));
    }

    public static void clear() {
        HOLDER.remove();
    }

    /**
     * 作用域式使用，自动还原前值，兼容嵌套。
     */
    public static <T> T runWith(TenantInfo info, Supplier<T> action) {
        TenantInfo previous = HOLDER.get();
        HOLDER.set(info);
        try {
            return action.get();
        } finally {
            if (previous == null) {
                HOLDER.remove();
            } else {
                HOLDER.set(previous);
            }
        }
    }

    public static void runWith(TenantInfo info, Runnable action) {
        runWith(info, () -> {
            action.run();
            return null;
        });
    }
}
