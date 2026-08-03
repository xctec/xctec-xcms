package com.df4j.xctec.xcms.base.tenant;

import com.df4j.xctec.xcms.base.security.SecurityContextAccessor;
import com.df4j.xctec.xcms.core.event.EventPublisher;
import com.df4j.xctec.xcms.core.exception.BizException;
import com.df4j.xctec.xcms.core.result.CommonErrorCode;
import com.df4j.xctec.xcms.core.tenant.TenantCacheService;
import com.df4j.xctec.xcms.core.tenant.TenantContext;
import com.df4j.xctec.xcms.core.tenant.TenantElevation;
import com.df4j.xctec.xcms.core.tenant.TenantGuard;
import com.df4j.xctec.xcms.core.tenant.TenantInfo;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

/**
 * 受控提权默认实现。
 *
 * 不对前端暴露「切换租户视角」接口。跨租户操作只能由服务端在明确业务语义下发起，且必留审计。
 * 通过 TenantContext.runWith 作用域式执行，自动还原上下文。
 */
@Component
@ConditionalOnMissingBean(TenantElevation.class)
public class DefaultTenantElevation implements TenantElevation {

    private final TenantGuard guard;
    private final TenantCacheService tenantCacheService;
    private final EventPublisher eventPublisher;
    private final SecurityContextAccessor securityContextAccessor;

    public DefaultTenantElevation(TenantGuard guard,
                                  TenantCacheService tenantCacheService,
                                  EventPublisher eventPublisher,
                                  SecurityContextAccessor securityContextAccessor) {
        this.guard = guard;
        this.tenantCacheService = tenantCacheService;
        this.eventPublisher = eventPublisher;
        this.securityContextAccessor = securityContextAccessor;
    }

    @Override
    public <T> T runAsTenant(Long targetTenantId, Supplier<T> action) {
        guard.assertManageable(targetTenantId);
        TenantInfo from = TenantContext.current().orElse(null);
        TenantInfo to = tenantCacheService.get(targetTenantId)
                .orElseThrow(() -> new BizException(CommonErrorCode.TENANT_NOT_FOUND, targetTenantId));
        Long operatorId = securityContextAccessor.currentUserId();
        // 审计事件：tenantId 为原租户，targetTenantId 为目标租户
        eventPublisher.publishAfterCommit(
                new TenantElevationEvent(operatorId, from == null ? null : from.tenantId(), targetTenantId));
        return TenantContext.runWith(to, action);
    }
}
