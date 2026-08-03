package com.df4j.xctec.xcms.notify.api;

/**
 * 通知渠道。当前预留站内信、邮件；后续可扩展短信 / 推送等，
 * 扩展时仅新增枚举值 + 对应发送实现，契约不变。
 */
public enum NotifyChannel {

    /** 站内信 */
    SITE,
    /** 邮件 */
    EMAIL
}
