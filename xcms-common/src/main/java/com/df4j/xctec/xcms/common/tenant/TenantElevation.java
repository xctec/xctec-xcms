package com.df4j.xctec.xcms.common.tenant;

import com.df4j.xctec.xcms.common.core.BizException;
import com.df4j.xctec.xcms.common.core.CommonErrorCode;
import com.df4j.xctec.xcms.common.core.ForbiddenException;
import com.df4j.xctec.xcms.common.event.EventPublisher;
import org.springframework.stereotype.Component;

/**
 * 受控提权：访问受限租户时显式提升 effectiveTenantId 并必留审计。
 * 提权需在 TenantGuard 通过后调用，且必须留下审计事件。
 */
@Component
public class TenantElevation {

    private final TenantGuard tenantGuard;
    private final TenantCacheService tenantCacheService;
    private final EventPublisher eventPublisher;

    public TenantElevation(TenantGuard tenantGuard,
                           TenantCacheService tenantCacheService,
                           EventPublisher eventPublisher) {
        this.tenantGuard = tenantGuard;
        this.tenantCacheService = tenantCacheService;
        this.eventPublisher = eventPublisher;
    }

    /**
     * 提权到 targetTenantId（必须是级联可达且已通过 Guard）。
     * 返回提权前上下文快照，便于还原。
     */
    public Snapshot elevateTo(Long targetTenantId) {
        tenantGuard.check(targetTenantId, true);
        Snapshot before = new Snapshot(
                TenantContext.current().getTenantId(),
                TenantContext.current().isElevated(),
                TenantContext.current().getPrincipal());
        TenantContext.current().setTenantId(targetTenantId);
        TenantContext.current().setElevated(true);
        // 必留审计
        Long operator = TenantContext.current().getPrincipal() == null
                ? null : TenantContext.current().getPrincipal().getUserId();
        eventPublisher.publishAfterCommit(
                new TenantElevationEvent(operator, targetTenantId));
        return before;
    }

    /** 还原提权上下文 */
    public void restore(Snapshot snapshotCtx) {
        if (snapshotCtx == null) {
            return;
        }
        TenantContext.current().setTenantId(snapshotCtx.tenantId());
        TenantContext.current().setElevated(snapshotCtx.elevated());
        TenantContext.current().setPrincipal(snapshotCtx.principal());
    }

    /** 提权前的上下文快照（记录值，非引用） */
    public record Snapshot(Long tenantId, boolean elevated, TenantContext.AuthPrincipalHolder principal) {
    }

    /** 提权审计事件 */
    public static class TenantElevationEvent extends com.df4j.xctec.xcms.common.event.AbstractEvent {
        private final Long targetTenantId;

        public TenantElevationEvent(Long operatorId, Long targetTenantId) {
            super(operatorId, targetTenantId);
            this.targetTenantId = targetTenantId;
        }

        public Long getTargetTenantId() {
            return targetTenantId;
        }
    }
}
