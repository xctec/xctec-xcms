package com.df4j.xctec.xcms.audit.api.event;

import com.df4j.xctec.xcms.core.event.AbstractEvent;
import lombok.Getter;

/**
 * 审计领域事件。业务侧经 {@code EventPublisher#publishAfterCommit} 投递，
 * 由 xcms-audit-impl 的监听器异步落库（{@code sys_audit_log}）。
 *
 * <p>携带设计文档要求的审计要素：操作人、租户、模块、动作、目标对象、
 * 入参摘要、结果、IP、traceId、耗时。</p>
 */
@Getter
public class AuditEvent extends AbstractEvent {

    /** 模块，如 system / auth / portal */
    private final String module;
    /** 动作，如 tenant.create / user.login */
    private final String action;
    /** 目标对象类型，如 tenant / user */
    private final String targetType;
    /** 目标对象 id */
    private final String targetId;
    /** 入参摘要（脱敏后的关键信息，非完整报文） */
    private final String paramsDigest;
    /** 执行结果：SUCCESS / FAIL */
    private final String result;
    /** 失败原因（result=FAIL 时） */
    private final String errorMessage;
    /** 来源 IP */
    private final String clientIp;
    /** 链路 id，便于跨服务串联 */
    private final String traceId;
    /** 业务耗时（毫秒） */
    private final Long durationMs;

    public AuditEvent(Long operatorId, Long tenantId, String module, String action,
                      String targetType, String targetId, String paramsDigest,
                      String result, String errorMessage, String clientIp,
                      String traceId, Long durationMs) {
        super(operatorId, tenantId);
        this.module = module;
        this.action = action;
        this.targetType = targetType;
        this.targetId = targetId;
        this.paramsDigest = paramsDigest;
        this.result = result;
        this.errorMessage = errorMessage;
        this.clientIp = clientIp;
        this.traceId = traceId;
        this.durationMs = durationMs;
    }
}
