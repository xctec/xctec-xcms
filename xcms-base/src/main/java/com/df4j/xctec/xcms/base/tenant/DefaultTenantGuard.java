package com.df4j.xctec.xcms.base.tenant;

import com.df4j.xctec.xcms.core.exception.BizException;
import com.df4j.xctec.xcms.core.exception.ForbiddenException;
import com.df4j.xctec.xcms.core.result.CommonErrorCode;
import com.df4j.xctec.xcms.core.tenant.TenantCacheService;
import com.df4j.xctec.xcms.core.tenant.TenantContext;
import com.df4j.xctec.xcms.core.tenant.TenantGuard;
import com.df4j.xctec.xcms.core.tenant.TenantInfo;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 管理域越权校验默认实现。
 *
 * 规则：目标必须是自身或子孙（级联下钻），否则拒绝。
 * 校验依据：target.path 是否以 current.scopePrefix() 开头。
 */
@Component
@ConditionalOnMissingBean(TenantGuard.class)
public class DefaultTenantGuard implements TenantGuard {

    private final TenantCacheService tenantCacheService;

    public DefaultTenantGuard(TenantCacheService tenantCacheService) {
        this.tenantCacheService = tenantCacheService;
    }

    @Override
    public void assertManageable(Long targetTenantId) {
        Long currentId = TenantContext.requireTenantId();
        if (Objects.equals(currentId, targetTenantId)) {
            return;
        }
        TenantInfo current = tenantCacheService.get(currentId)
                .orElseThrow(() -> new BizException(CommonErrorCode.TENANT_NOT_FOUND, currentId));
        TenantInfo target = tenantCacheService.get(targetTenantId)
                .orElseThrow(() -> new BizException(CommonErrorCode.TENANT_NOT_FOUND, targetTenantId));
        // 目标必须是自身或子孙：target.path 以 current.scopePrefix() 开头
        if (!target.path().startsWith(current.scopePrefix())) {
            throw new ForbiddenException(CommonErrorCode.TENANT_NOT_MANAGEABLE, targetTenantId);
        }
    }

    @Override
    public String manageableScopePrefix() {
        Long currentId = TenantContext.requireTenantId();
        return tenantCacheService.get(currentId)
                .orElseThrow(() -> new BizException(CommonErrorCode.TENANT_NOT_FOUND, currentId))
                .scopePrefix();
    }
}
