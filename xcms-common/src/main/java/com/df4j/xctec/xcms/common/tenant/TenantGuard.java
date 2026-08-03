package com.df4j.xctec.xcms.common.tenant;

import com.df4j.xctec.xcms.common.core.BizException;
import com.df4j.xctec.xcms.common.core.CommonErrorCode;
import com.df4j.xctec.xcms.common.core.ForbiddenException;
import com.df4j.xctec.xcms.common.core.UnauthorizedException;
import org.springframework.stereotype.Component;

/**
 * 管理域越权校验。回答"当前主体能否操作目标租户"。
 * 规则：
 *  - 未认证 -> Unauthorized。
 *  - 目标租户是主体所在租户的祖先或自身 -> 允许（级联上钻）。
 *  - 受限租户（需提权）且未 elevated -> 拒绝，要求显式提权。
 */
@Component
public class TenantGuard {

    private final TenantCacheService tenantCacheService;

    public TenantGuard(TenantCacheService tenantCacheService) {
        this.tenantCacheService = tenantCacheService;
    }

    /**
     * 校验主体能否操作 targetTenantId。
     * @param requireElevation 目标是否为受限租户（如平台级），需显式提权
     */
    public void check(Long targetTenantId, boolean requireElevation) {
        TenantContext.AuthPrincipalHolder principal = TenantContext.current().getPrincipal();
        if (principal == null) {
            throw new UnauthorizedException(CommonErrorCode.SECURITY_UNAUTHORIZED);
        }
        Long selfTenant = principal.getEffectiveTenantId();
        if (selfTenant == null) {
            throw new UnauthorizedException(CommonErrorCode.SECURITY_UNAUTHORIZED);
        }
        if (targetTenantId == null) {
            throw new ForbiddenException(CommonErrorCode.TENANT_FORBIDDEN, "null");
        }
        // 级联：目标必须是自身或祖先
        if (!selfTenant.equals(targetTenantId)
                && !tenantCacheService.isAncestor(selfTenant, targetTenantId)) {
            throw new ForbiddenException(CommonErrorCode.TENANT_FORBIDDEN, targetTenantId);
        }
        // 受限租户需显式提权
        if (requireElevation && !TenantContext.current().isElevated()) {
            throw new ForbiddenException(CommonErrorCode.TENANT_ELEVATION_REQUIRED, targetTenantId);
        }
    }

    public void check(Long targetTenantId) {
        check(targetTenantId, false);
    }
}
