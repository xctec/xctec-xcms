package com.df4j.xctec.xcms.common.configuration;

import com.df4j.xctec.xcms.core.context.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class TenantContextConfiguration {


    @Bean
    @ConditionalOnMissingBean(name = "tenantContextAccessor")
    public ContextAccessor<Long> threadLocalTenantContextAccessor() {
        log.info("create ThreadLocalContextAccessor");
        return new ThreadLocalContextAccessor<>();
    }


    @Bean
    @ConditionalOnBean(name = "tenantContextAccessor")
    public TenantContextsBinder securityContextsBinder(ContextAccessor<Long> contextAccessor) {
        return new TenantContextsBinder(contextAccessor);
    }

    public static class TenantContextsBinder implements SmartInitializingSingleton {

        private final ContextAccessor<Long> contextAccessor;

        public TenantContextsBinder(ContextAccessor<Long> contextAccessor) {
            this.contextAccessor = contextAccessor;
        }

        @Override
        public void afterSingletonsInstantiated() {
            TenantContextUtils.bind(contextAccessor);
            log.info("bind ContextAccessor<Long>  to TenantContextUtils. type: {}",
                    contextAccessor.getClass().getName());
        }
    }
}
