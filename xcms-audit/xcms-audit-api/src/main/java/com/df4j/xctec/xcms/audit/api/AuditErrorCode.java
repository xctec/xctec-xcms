package com.df4j.xctec.xcms.audit.api;

import com.df4j.xctec.xcms.core.result.ErrorCode;

/**
 * audit 模块专属错误码（审计日志查询）。
 * 审计落库失败属于基础设施问题，由 impl 内部兜底重试 / 告警，不在此暴露对外错误码。
 */
public enum AuditErrorCode implements ErrorCode {

    LOG_NOT_FOUND("audit.log.notFound", "审计记录不存在：{0}"),
    QUERY_FORBIDDEN("audit.query.forbidden", "无权查询该租户审计记录");

    private final String code;
    private final String messageTemplate;

    AuditErrorCode(String code, String messageTemplate) {
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
