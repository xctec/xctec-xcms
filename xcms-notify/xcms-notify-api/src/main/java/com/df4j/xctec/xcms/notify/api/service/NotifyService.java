package com.df4j.xctec.xcms.notify.api.service;

import com.df4j.xctec.xcms.notify.api.dto.NotifyMessageDto;

/**
 * 通知契约（接口）。实现位于 xcms-notify-impl，遵循接口先行原则。
 * 当前为接口预留阶段，impl 提供站内信 / 邮件的最小可用实现。
 */
public interface NotifyService {

    /**
     * 同步发送单条通知。
     *
     * @param dto 消息入参
     * @return 消息 id
     */
    String send(NotifyMessageDto dto);

    /**
     * 异步发送（经事件机制投递，主链路零阻塞）。
     *
     * @param dto 消息入参
     */
    void sendAsync(NotifyMessageDto dto);
}
