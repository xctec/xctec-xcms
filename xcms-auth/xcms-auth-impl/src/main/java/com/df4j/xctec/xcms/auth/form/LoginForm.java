package com.df4j.xctec.xcms.auth.form;

import lombok.Data;

/**
 * 登录表单。
 * <p>租户标识不在此处传，而是从请求上下文获取（请求头 X-Tenant-Id 经 TenantContextFilter 写入 TenantContext）。
 * 登录基于 (tenantId, username) 唯一约束，tenantId 取自上下文。</p>
 */
@Data
public class LoginForm {

    private String username;

    private String password;
}
