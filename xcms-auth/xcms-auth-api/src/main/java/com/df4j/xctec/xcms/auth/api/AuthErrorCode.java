package com.df4j.xctec.xcms.auth.api;

import com.df4j.xctec.xcms.core.result.ErrorCode;

/**
 * auth 模块专属错误码（登录、令牌、鉴权）。
 * 公共 / 租户上下文相关错误码见 {@link com.df4j.xctec.xcms.core.result.CommonErrorCode}；
 * 令牌过期见 {@code auth.token.expired}（此处一并声明，便于实现层统一引用）。
 */
public enum AuthErrorCode implements ErrorCode {

    CREDENTIAL_INVALID("auth.credential.invalid", "用户名或密码错误"),
    ACCOUNT_DISABLED("auth.account.disabled", "账号已被停用"),
    ACCOUNT_LOCKED("auth.account.locked", "账号已被锁定"),
    TENANT_DISABLED("auth.tenant.disabled", "租户已被停用"),
    TOKEN_EXPIRED("auth.token.expired", "登录已过期"),
    TOKEN_INVALID("auth.token.invalid", "令牌无效"),
    TOKEN_REFRESH_FAILED("auth.token.refreshFailed", "令牌刷新失败"),
    PERMISSION_DENIED("auth.permission.denied", "无权访问：{0}"),
    NOT_AUTHENTICATED("auth.notAuthenticated", "未登录或登录已失效");

    private final String code;
    private final String messageTemplate;

    AuthErrorCode(String code, String messageTemplate) {
        this.code = code;
        this.messageTemplate = messageTemplate;
    }

    @Override
    public String code() {
        return code;
    }

    @Override
    public String messageTemplate() {
        return messageTemplate;
    }
}
