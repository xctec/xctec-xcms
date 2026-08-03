package com.df4j.xctec.xcms.notify.api.dto;

import com.df4j.xctec.xcms.notify.api.NotifyChannel;
import lombok.Data;

import java.io.Serializable;

/**
 * 通知消息记录视图（查询返回）。
 */
@Data
public class NotifyMessageVo implements Serializable {

    private String messageId;
    private NotifyChannel channel;
    private Long recipientId;
    private String title;
    private String content;
    /** 状态：PENDING / SENT / FAILED */
    private String status;
    private String errorMessage;
    private Long createdAt;
    private Long sentAt;
}
