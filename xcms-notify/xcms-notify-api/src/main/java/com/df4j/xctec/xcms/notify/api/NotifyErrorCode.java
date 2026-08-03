package com.df4j.xctec.xcms.notify.api;

import com.df4j.xctec.xcms.core.result.ErrorCode;

/**
 * notify 模块专属错误码（站内信、邮件等通知）。
 * 当前为接口预留阶段，错误码覆盖发送与渠道相关异常。
 */
public enum NotifyErrorCode implements ErrorCode {

    CHANNEL_UNSUPPORTED("notify.channel.unsupported", "不支持的通知渠道：{0}"),
    RECIPIENT_EMPTY("notify.recipient.empty", "接收人不能为空"),
    TEMPLATE_NOT_FOUND("notify.template.notFound", "通知模板不存在：{0}"),
    SEND_FAILED("notify.send.failed", "通知发送失败：{0}");

    private final String code;
    private final String messageTemplate;

    NotifyErrorCode(String code, String messageTemplate) {
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
