package com.df4j.xctec.xcms.base.tenant;

import com.df4j.xctec.xcms.core.event.AbstractEvent;

/**
 * 受控提权审计事件。
 * tenantId 为提权发生的原租户，targetTenantId 为提权目标租户。
 */
public class TenantElevationEvent extends AbstractEvent {

    private final Long targetTenantId;

    public TenantElevationEvent(Long operatorId, Long fromTenantId, Long targetTenantId) {
        super(operatorId, fromTenantId);
        this.targetTenantId = targetTenantId;
    }

    public Long getTargetTenantId() {
        return targetTenantId;
    }
}
