package com.df4j.xctec.xcms.audit.api.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 审计日志记录视图（查询返回）。
 */
@Data
public class AuditLogVo implements Serializable {

    private String eventId;
    private Long operatorId;
    private Long tenantId;
    private String module;
    private String action;
    private String targetType;
    private String targetId;
    private String paramsDigest;
    private String result;
    private String errorMessage;
    private String clientIp;
    private String traceId;
    private Long durationMs;
    private Long occurredAt;
}
