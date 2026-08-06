package com.df4j.xctec.xcms.core.context;

import java.util.function.Supplier;

/**
 * 安全上下文的静态便捷入口。
 *
 * <p>内部持有被 Spring 注入的 {@link SecurityContextAccessor} 实例，对外提供静态方法，
 * 免去业务代码中到处注入访问器。等价于 {@code SecurityContextAccessor} 的“门面”。
 *
 * <p>用法：
 * <pre>{@code
 *     Long tenantId = SecurityContexts.requireTenantId();
 *     SecurityContext.LoginUser user = SecurityContexts.currentUser();
 * }</pre>
 *
 * <p>注意：本类不持有任何线程状态，线程状态全部由 {@link SecurityContextAccessor}
 * 的实现维护，因此本类可安全作为静态门面，不会退化为旧的 {@code ContextHolder} 形态。
 */
public final class SecurityContexts {

    private static volatile SecurityContextAccessor instance;

    private SecurityContexts() {
    }

    /**
     * 将实现绑定到静态门面。供 Spring 装配器（{@code SecurityContextAutoConfiguration}）在容器就绪后调用，
     * 由 {@link SecurityContextAccessor} 的实现（默认 {@code ThreadLocal}）注入，业务代码不应直接调用。
     */
    public static void bind(SecurityContextAccessor accessor) {
        instance = accessor;
    }

    private static SecurityContextAccessor current() {
        SecurityContextAccessor accessor = instance;
        if (accessor == null) {
            throw new IllegalStateException("SecurityContextAccessor 尚未初始化，请确认已扫描并加载 SecurityContextAutoConfiguration");
        }
        return accessor;
    }

    public static SecurityContext getOrNull() {
        return current().getOrNull();
    }

    public static SecurityContext get() {
        return current().get();
    }

    public static SecurityContext.TenantInfo currentTenant() {
        return current().currentTenant();
    }

    public static SecurityContext.LoginUser currentUser() {
        return current().currentUser();
    }

    public static Long requireTenantId() {
        return current().requireTenantId();
    }

    public static Long requireUserId() {
        return current().requireUserId();
    }

    public static void set(SecurityContext context) {
        current().set(context);
    }

    public static void clear() {
        current().clear();
    }

    public static boolean isElevated() {
        return current().isElevated();
    }

    public static SecurityContext.ElevatedScope getElevatedScope() {
        return current().getElevatedScope();
    }

    public static void elevateTo(Long targetTenantId) {
        current().elevateTo(targetTenantId);
    }

    public static void elevateTo(SecurityContext.ElevatedScope scope) {
        current().elevateTo(scope);
    }

    public static void runElevated(Long targetTenantId, Runnable action) {
        current().runElevated(targetTenantId, action);
    }

    public static <T> T runElevated(Long targetTenantId, Supplier<T> action) {
        return current().runElevated(targetTenantId, action);
    }

    public static void runElevated(SecurityContext.ElevatedScope scope, Runnable action) {
        current().runElevated(scope, action);
    }

    public static <T> T runElevated(SecurityContext.ElevatedScope scope, Supplier<T> action) {
        return current().runElevated(scope, action);
    }
}
