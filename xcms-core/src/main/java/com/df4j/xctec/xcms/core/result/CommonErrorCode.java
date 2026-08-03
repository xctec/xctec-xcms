package com.df4j.xctec.xcms.core.result;

/**
 * 公共模块及租户/安全相关的标准错误码。
 * system 模块自身的错误码放在其专属枚举，不在此处。
 */
public enum CommonErrorCode implements ErrorCode {

    VALIDATION_FAILED("common.param.invalid", "参数校验失败：{0}"),
    SYSTEM_ERROR("common.system.error", "系统繁忙，请稍后重试"),
    TENANT_CONTEXT_MISSING("common.tenant.contextMissing", "租户上下文缺失"),
    TENANT_NOT_MANAGEABLE("common.tenant.notManageable", "无权管理目标租户：{0}"),
    TENANT_NOT_FOUND("common.tenant.notFound", "租户不存在：{0}"),
    TREE_MOVE_TO_SELF("common.tree.moveToSelf", "不能移动到自身"),
    TREE_MOVE_TO_DESCENDANT("common.tree.moveToDescendant", "不能移动到自己的子孙下"),
    RATE_LIMITED("common.rate.limited", "请求过于频繁，请稍后再试");

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
