package com.df4j.xctec.xcms.web.security;

import com.df4j.xctec.xcms.core.exception.ForbiddenException;
import com.df4j.xctec.xcms.core.exception.UnauthorizedException;
import com.df4j.xctec.xcms.core.result.CommonErrorCode;
import com.df4j.xctec.xcms.core.security.AuthPrincipal;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Set;

/**
 * 权限校验 AOP 切面。
 *
 * 拦截标注了 @RequiresPermission 的方法或类，从 SecurityContextHolder 读取当前主体的权限集合，
 * 按注解声明的权限码与逻辑关系校验。
 *
 * - 未认证（principal 为空）→ UnauthorizedException
 * - 权限不足 → ForbiddenException
 */
@Aspect
@Component
public class PermissionAspect {

    @Before("@within(com.df4j.xctec.xcms.web.security.RequiresPermission) "
            + "|| @annotation(com.df4j.xctec.xcms.web.security.RequiresPermission)")
    public void checkPermission(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        // 方法上的注解优先于类上的注解
        RequiresPermission annotation = method.getAnnotation(RequiresPermission.class);
        if (annotation == null) {
            annotation = method.getDeclaringClass().getAnnotation(RequiresPermission.class);
        }
        if (annotation == null) {
            return;
        }

        AuthPrincipal principal = SecurityContextHolder.get()
                .orElseThrow(() -> new UnauthorizedException(CommonErrorCode.TENANT_CONTEXT_MISSING));

        Set<String> permissions = principal.permissions();
        String[] required = annotation.value();
        RequiresPermission.Logical logical = annotation.logical();

        boolean granted;
        if (logical == RequiresPermission.Logical.AND) {
            granted = Arrays.stream(required).allMatch(p -> permissions != null && permissions.contains(p));
        } else {
            granted = Arrays.stream(required).anyMatch(p -> permissions != null && permissions.contains(p));
        }

        if (!granted) {
            throw new ForbiddenException(CommonErrorCode.TENANT_NOT_MANAGEABLE, Arrays.toString(required));
        }
    }
}
