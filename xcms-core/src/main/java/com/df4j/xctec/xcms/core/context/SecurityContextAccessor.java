package com.df4j.xctec.xcms.core.context;

import com.df4j.xctec.xcms.core.exception.SecurityContextException;

import java.util.function.Supplier;

/**
 * 安全上下文访问接口。
 *
 * <p>将上下文的“存储实现”与“访问方式”解耦：业务代码依赖接口而非具体存储
 * （{@code ThreadLocal} 或虚拟线程下的 {@code ScopedValue}），便于替换与单测。
 *
 * <p>只有三个方法直接触碰底层存储原语（{@link #getOrNull()} / {@link #set(SecurityContext)} /
 * {@link #clear()}），必须由各存储实现提供；其余访问与提权方法均为 {@code default} 实现，
 * 基于这三个原语组合，所有实现自动复用，无需重复编码。
 *
 * <p>注入使用：{@code @Autowired SecurityContextAccessor accessor}。
 * 静态便捷入口见 {@link SecurityContexts}。
 */
public interface SecurityContextAccessor {

    /**
     * 获取当前上下文，未设置时返回 {@code null}。
     * 底层存储原语，由各实现（ThreadLocal / ScopedValue）自行提供。
     */
    SecurityContext getOrNull();

    /**
     * 写入当前上下文。底层存储原语，由各实现自行提供。
     */
    void set(SecurityContext context);

    /**
     * 清空当前上下文，防止线程复用导致串号。底层存储原语，由各实现自行提供。
     */
    void clear();

    /**
     * 获取当前上下文，未设置时抛出 {@link SecurityContextException#noContext()}。
     */
    default SecurityContext get() {
        SecurityContext context = getOrNull();
        if (context == null) {
            throw SecurityContextException.noContext();
        }
        return context;
    }

    /**
     * 获取当前租户信息，未设置时返回 {@code null}。
     */
    default SecurityContext.TenantInfo currentTenant() {
        SecurityContext context = getOrNull();
        return context == null ? null : context.getTenantInfo();
    }

    /**
     * 获取当前登录用户，未设置时返回 {@code null}。
     */
    default SecurityContext.LoginUser currentUser() {
        SecurityContext context = getOrNull();
        return context == null ? null : context.getLoginUser();
    }

    /**
     * 获取当前租户 ID，缺失时抛出 {@link SecurityContextException#noTenant()}（越权/无租户的业务语义）。
     */
    default Long requireTenantId() {
        SecurityContext.TenantInfo tenant = currentTenant();
        if (tenant == null || tenant.getTenantId() == null) {
            throw SecurityContextException.noTenant();
        }
        return tenant.getTenantId();
    }

    /**
     * 获取当前用户 ID，缺失时抛出 {@link SecurityContextException#noLogin()}（越权/未登录的业务语义）。
     */
    default Long requireUserId() {
        SecurityContext.LoginUser user = currentUser();
        if (user == null || user.getUserId() == null) {
            throw SecurityContextException.noLogin();
        }
        return user.getUserId();
    }

    /**
     * 当前上下文是否处于提权状态。
     *
     * <p>提权仅放松“数据作用域”（越过默认租户边界），不改变“身份”。
     * 无论是否提权，{@link #requireUserId()} / {@link #requireTenantId()} 仍要求身份存在，用于审计。
     */
    default boolean isElevated() {
        SecurityContext context = getOrNull();
        return context != null && context.isElevated();
    }

    /**
     * 获取提权作用域（含目标租户等约束），未提权时返回 {@code null}。
     * 消费层据此决定数据放行边界。
     */
    default SecurityContext.ElevatedScope getElevatedScope() {
        SecurityContext context = getOrNull();
        return context == null ? null : context.getElevatedScope();
    }

    /**
     * 受控提权：提权到指定目标租户。等价于 {@link #elevateTo(SecurityContext.ElevatedScope)} 的单租户便捷写法。
     *
     * <p>前置条件：当前线程必须已存在安全上下文（已登录/已认证），否则抛出
     * {@link SecurityContextException#noContext()}。
     *
     * <p>语义红线（供数据过滤层消费）：
     * <ul>
     *     <li>{@code elevated=false}：数据过滤层<b>强制</b>按 {@code tenantInfo.tenantId} 过滤；</li>
     *     <li>{@code elevated=true}：数据过滤层按 {@code elevatedScope} 放行目标租户。</li>
     * </ul>
     *
     * <p>注意：提权作用于整个上下文对象，跨同请求内后续操作均生效，
     * 需自行复位或使用 {@link #runElevated(Long, Runnable)} 作用域块以避免遗忘。
     *
     * @param targetTenantId 提权目标租户 ID，不可为 {@code null}
     */
    default void elevateTo(Long targetTenantId) {
        if (targetTenantId == null) {
            throw SecurityContextException.noTargetTenant();
        }
        elevateTo(SecurityContext.ElevatedScope.ofTenant(targetTenantId));
    }

    /**
     * 受控提权：按给定 {@link SecurityContext.ElevatedScope} 提权，可携带多租户、原因、审计人、有效期等扩展约束。
     *
     * @param scope 提权作用域，{@code scope.targetTenantId} 不可为 {@code null}
     */
    default void elevateTo(SecurityContext.ElevatedScope scope) {
        SecurityContext context = getOrNull();
        if (context == null) {
            throw SecurityContextException.noContext();
        }
        if (scope == null) {
            throw SecurityContextException.noTargetTenant();
        }
        context.setElevated(true);
        context.setElevatedScope(scope);
        set(context);
    }

    /**
     * 提权作用域块（单租户）：在当前上下文基础上临时提权到 {@code targetTenantId} 并执行动作，
     * 执行完毕（无论成功或异常）自动复位提权状态，避免同请求内遗留提权态。
     *
     * @param targetTenantId 提权目标租户 ID
     * @param action         需执行的动作
     */
    default void runElevated(Long targetTenantId, Runnable action) {
        if (targetTenantId == null) {
            throw SecurityContextException.noTargetTenant();
        }
        runElevated(SecurityContext.ElevatedScope.ofTenant(targetTenantId), action);
    }

    /**
     * 带返回值的提权作用域块（单租户），语义同 {@link #runElevated(Long, Runnable)}。
     *
     * @param targetTenantId 提权目标租户 ID
     * @param action         需执行的带返回值动作
     * @param <T>            返回值类型
     * @return 动作的返回值
     */
    default <T> T runElevated(Long targetTenantId, Supplier<T> action) {
        if (targetTenantId == null) {
            throw SecurityContextException.noTargetTenant();
        }
        return runElevated(SecurityContext.ElevatedScope.ofTenant(targetTenantId), action);
    }

    /**
     * 提权作用域块：按给定 {@link SecurityContext.ElevatedScope} 临时提权并执行动作，
     * 执行完毕（无论成功或异常）自动复位提权状态，避免同请求内遗留提权态。
     *
     * @param scope  提权作用域
     * @param action 需执行的动作
     */
    default void runElevated(SecurityContext.ElevatedScope scope, Runnable action) {
        runElevated(scope, () -> {
            action.run();
            return null;
        });
    }

    /**
     * 带返回值的提权作用域块，语义同 {@link #runElevated(SecurityContext.ElevatedScope, Runnable)}。
     *
     * @param scope  提权作用域
     * @param action 需执行的带返回值动作
     * @param <T>    返回值类型
     * @return 动作的返回值
     */
    default <T> T runElevated(SecurityContext.ElevatedScope scope, Supplier<T> action) {
        SecurityContext context = getOrNull();
        if (context == null) {
            throw SecurityContextException.noContext();
        }
        if (scope == null) {
            throw SecurityContextException.noTargetTenant();
        }
        boolean prevElevated = context.isElevated();
        SecurityContext.ElevatedScope prevScope = context.getElevatedScope();
        context.setElevated(true);
        context.setElevatedScope(scope);
        set(context);
        try {
            return action.get();
        } finally {
            context.setElevated(prevElevated);
            context.setElevatedScope(prevScope);
            set(context);
        }
    }
}
