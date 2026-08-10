package com.df4j.xctec.xcms.common.configuration;

import com.df4j.xctec.xcms.core.context.tenant.TenantContextAccessor;
import com.df4j.xctec.xcms.core.context.tenant.TenantContextBinder;
import com.df4j.xctec.xcms.core.context.tenant.ThreadLocalTenantContextAccessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class TenantContextConfiguration {


    @Bean
    @ConditionalOnMissingBean(name = "tenantContextAccessor")
    public TenantContextAccessor tenantContextAccessor() {
        log.info("create ThreadLocalTenantContextAccessor");
        return new ThreadLocalTenantContextAccessor();
    }


    @Bean
    @ConditionalOnBean(name = "tenantContextAccessor")
    public TenantContextBinder tenantContextsBinder(TenantContextAccessor contextAccessor) {
        return new TenantContextBinder(contextAccessor);
    }

}
