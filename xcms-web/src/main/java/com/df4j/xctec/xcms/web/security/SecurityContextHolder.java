package com.df4j.xctec.xcms.web.security;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.df4j.xctec.xcms.base.security.SecurityContextAccessor;
import com.df4j.xctec.xcms.core.security.AuthPrincipal;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * 基于 TTL 的安全上下文持有器。
 *
 * 持有当前请求的 AuthPrincipal，跨线程透传。
 * 由 TokenAuthenticationFilter 填充，TenantContextFilter 读取。
 * 实现 base 层的 SecurityContextAccessor 接口，供审计字段填充、事件 operatorId 等使用。
 */
@Component
@ConditionalOnMissingBean(SecurityContextAccessor.class)
public class SecurityContextHolder implements SecurityContextAccessor {

    private static final ThreadLocal<AuthPrincipal> HOLDER = new TransmittableThreadLocal<>();

    public static void set(AuthPrincipal principal) {
        HOLDER.set(principal);
    }

    public static void clear() {
        HOLDER.remove();
    }

    /**
     * 静态访问当前主体，供过滤器/切面等无注入场景使用。
     */
    public static Optional<AuthPrincipal> get() {
        return Optional.ofNullable(HOLDER.get());
    }

    @Override
    public Optional<AuthPrincipal> currentPrincipal() {
        return get();
    }
}
