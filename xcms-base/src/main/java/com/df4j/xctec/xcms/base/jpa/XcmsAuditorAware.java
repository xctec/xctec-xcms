package com.df4j.xctec.xcms.base.jpa;

import com.df4j.xctec.xcms.base.security.SecurityContextAccessor;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * 审计字段填充：从 SecurityContextAccessor 获取当前用户 id，填充 createdBy / updatedBy。
 */
@Component
public class XcmsAuditorAware implements AuditorAware<Long> {

    private final SecurityContextAccessor securityContextAccessor;

    public XcmsAuditorAware(SecurityContextAccessor securityContextAccessor) {
        this.securityContextAccessor = securityContextAccessor;
    }

    @Override
    public Optional<Long> getCurrentAuditor() {
        return securityContextAccessor.currentPrincipal().map(p -> p.userId());
    }
}
