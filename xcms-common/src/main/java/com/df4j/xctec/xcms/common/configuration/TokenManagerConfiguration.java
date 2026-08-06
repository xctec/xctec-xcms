package com.df4j.xctec.xcms.common.configuration;

import com.df4j.xctec.xcms.common.security.token.MemoryTokenManager;
import com.df4j.xctec.xcms.common.security.token.TokenManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TokenManagerConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public TokenManager tokenManager() {
        return new MemoryTokenManager();
    }
}
