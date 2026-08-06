package com.df4j.xctec.xcms.core.context;

/**
 * 基于 {@link ThreadLocal} 的安全上下文访问实现（默认实现）。
 *
 * <p>适用于平台线程（Tomcat/Jetty 默认线程池）。线程池复用场景下，必须由
 * {@code SecurityContextCleanupInterceptor}（{@code afterCompletion}）统一调用
 * {@link #clear()} 清理，否则会串号。
 *
 * <p>仅提供三个底层存储原语（{@code getOrNull}/{@code set}/{@code clear}）；
 * 其余访问与提权方法均为 {@link SecurityContextAccessor} 的 {@code default} 实现，此处无需重复。
 */
public class ThreadLocalSecurityContextAccessor implements SecurityContextAccessor {

    private static final ThreadLocal<SecurityContext> CONTEXT = new ThreadLocal<>();

    @Override
    public SecurityContext getOrNull() {
        return CONTEXT.get();
    }

    @Override
    public void set(SecurityContext context) {
        CONTEXT.set(context);
    }

    @Override
    public void clear() {
        CONTEXT.remove();
    }
}
