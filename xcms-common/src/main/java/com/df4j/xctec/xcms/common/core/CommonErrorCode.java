package com.df4j.xctec.xcms.common.core;

/**
 * 公共模块及租户/安全相关的标准错误码。
 * system 模块自身的错误码放在其专属枚举，不在此处。
 */
public enum CommonErrorCode implements ErrorCode {

    COMMON_SUCCESS("common.success", "成功"),
    COMMON_ERROR("common.error", "系统错误"),
    COMMON_VALIDATION_ERROR("common.validation.error", "参数校验失败：{0}"),
    COMMON_ARGUMENT_ERROR("common.argument.error", "参数错误：{0}"),
    COMMON_NOT_FOUND("common.not.found", "资源不存在：{0}"),

    SECURITY_UNAUTHORIZED("security.unauthorized", "未认证或登录已过期"),
    SECURITY_TOKEN_INVALID("security.token.invalid", "无效的令牌"),
    SECURITY_TOKEN_EXPIRED("security.token.expired", "令牌已过期"),

    TENANT_NOT_FOUND("tenant.not.found", "租户不存在：{0}"),
    TENANT_FORBIDDEN("tenant.forbidden", "无权访问租户：{0}"),
    TENANT_ELEVATION_REQUIRED("tenant.elevation.required", "操作受限租户需要显式提权：{0}"),
    TENANT_CYCLE("tenant.cycle", "租户迁移将形成环路"),
    TENANT_NOT_LEAF("tenant.not.leaf", "租户存在子节点，无法作为叶子迁移目标"),

    COMMON_RATE_LIMITED("common.rate.limited", "请求过于频繁，请稍后再试");

    private final String code;
    private final String messageTemplate;

    CommonErrorCode(String code, String messageTemplate) {
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
