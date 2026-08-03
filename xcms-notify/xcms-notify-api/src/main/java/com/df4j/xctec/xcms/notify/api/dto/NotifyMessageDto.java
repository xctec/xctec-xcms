package com.df4j.xctec.xcms.notify.api.dto;

import com.df4j.xctec.xcms.notify.api.NotifyChannel;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 通知消息入参。支持模板 + 占位符渲染，或直接传入标题 / 正文。
 */
@Data
public class NotifyMessageDto implements Serializable {

    /** 通知渠道 */
    @NotNull(message = "通知渠道不能为空")
    private NotifyChannel channel;

    /** 接收人（用户 id 列表；邮件渠道可同时承载邮箱，由 impl 解析） */
    @NotEmpty(message = "接收人不能为空")
    private List<Long> recipientIds;

    /** 模板编码，二选一：模板渲染 */
    private String templateCode;

    /** 模板占位符参数 */
    private Map<String, Object> templateParams;

    /** 直接标题，二选一：直接内容 */
    private String title;

    /** 直接正文 */
    private String content;
}
