package com.df4j.xctec.xcms.common.configuration;

import com.df4j.xctec.xcms.core.context.token.ThreadLocalTokenContextAccessor;
import com.df4j.xctec.xcms.core.context.token.TokenContextAccessor;
import com.df4j.xctec.xcms.core.context.token.TokenContextBinder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class TokenContextConfiguration {


    @Bean
    @ConditionalOnMissingBean(name = "tokenContextAccessor")
    public TokenContextAccessor tokenContextAccessor() {
        log.info("create ThreadLocalTokenContextAccessor");
        return new ThreadLocalTokenContextAccessor();
    }


    @Bean
    @ConditionalOnBean(name = "tokenContextAccessor")
    public TokenContextBinder tokenContextBinder(TokenContextAccessor contextAccessor) {
        return new TokenContextBinder(contextAccessor);
    }

}
