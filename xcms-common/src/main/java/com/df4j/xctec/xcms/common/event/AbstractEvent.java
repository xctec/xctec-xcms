package com.df4j.xctec.xcms.common.event;

import java.io.Serializable;
import java.time.Instant;

/**
 * 领域事件基类。携带事件唯一 id 与触发时间，便于审计与幂等。
 */
public abstract class AbstractEvent implements Serializable {

    private final String eventId;
    private final Instant occurredAt;
    private final Long operatorId;
    private final Long tenantId;

    protected AbstractEvent(Long operatorId, Long tenantId) {
        this.eventId = java.util.UUID.randomUUID().toString().replace("-", "");
        this.occurredAt = Instant.now();
        this.operatorId = operatorId;
        this.tenantId = tenantId;
    }

    public String getEventId() {
        return eventId;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }

    public Long getOperatorId() {
        return operatorId;
    }

    public Long getTenantId() {
        return tenantId;
    }
}
