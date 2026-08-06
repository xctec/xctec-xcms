package com.df4j.xctec.xcms.common.configuration;

import com.df4j.xctec.xcms.core.context.SecurityContextAccessor;
import com.df4j.xctec.xcms.core.context.SecurityContexts;
import com.df4j.xctec.xcms.core.context.ThreadLocalSecurityContextAccessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class SecurityContextConfiguration {


    @Bean
    @ConditionalOnMissingBean
    public SecurityContextAccessor threadLocalSecurityContextAccessor() {
        log.info("create ThreadLocalSecurityContextAccessor");
        return new ThreadLocalSecurityContextAccessor();
    }


    @Bean
    @ConditionalOnBean(SecurityContextAccessor.class)
    public SecurityContextsBinder securityContextsBinder(SecurityContextAccessor securityContextAccessor) {
        return new SecurityContextsBinder(securityContextAccessor);
    }

    public static class SecurityContextsBinder implements SmartInitializingSingleton {

        private final SecurityContextAccessor securityContextAccessor;

        public SecurityContextsBinder(SecurityContextAccessor securityContextAccessor) {
            this.securityContextAccessor = securityContextAccessor;
        }

        @Override
        public void afterSingletonsInstantiated() {
            SecurityContexts.bind(securityContextAccessor);
            log.info("bind SecurityContextAccessor to SecurityContexts. type: {}",
                    securityContextAccessor.getClass().getName());
        }
    }
}
