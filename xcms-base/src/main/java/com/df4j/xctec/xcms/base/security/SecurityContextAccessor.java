package com.df4j.xctec.xcms.base.security;

import com.df4j.xctec.xcms.core.security.AuthPrincipal;

import java.util.Optional;

/**
 * 认证上下文访问抽象。
 * 由 xcms-web 的过滤器链填充，base 层通过此接口读取当前主体（用于审计字段填充、事件 operatorId 等）。
 */
public interface SecurityContextAccessor {

    Optional<AuthPrincipal> currentPrincipal();

    default Long currentUserId() {
        return currentPrincipal().map(AuthPrincipal::userId).orElse(null);
    }
}
