package com.df4j.xctec.xcms.web.sort;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * SortResolver 自动配置。
 *
 * 提供单例 SortResolver，各 impl 模块注入后通过 register() 注册各自的排序白名单。
 */
@Configuration
public class SortResolverConfig {

    @Bean
    @ConditionalOnMissingBean
    public SortResolver sortResolver() {
        return new SortResolver();
    }
}
