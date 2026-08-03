package com.df4j.xctec.xcms.web.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 功能权限注解：标注在 Controller 方法或类上，声明所需权限码。
 *
 * 权限码格式：{模块}:{资源}:{操作}，如 system:tenant:create。
 * 由 PermissionAspect 拦截校验，从 SecurityContextHolder 读取当前主体的 permissions 集合判断。
 *
 * 标注在类上时，对类内所有方法生效；方法上的注解优先于类上的注解。
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresPermission {

    /**
     * 权限码，支持多个（AND 关系，全部满足才放行）。
     */
    String[] value();

    /**
     * 多个权限码的逻辑关系：AND（默认）/ OR。
     */
    Logical logical() default Logical.AND;

    enum Logical {
        AND, OR
    }
}
