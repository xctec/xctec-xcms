package com.df4j.xctec.xcms.common.jpa.id;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 * ID 生成器自动配置：根据 xcms.id.strategy 选择默认策略。
 * 号段策略需要数据源；雪花 / 自增无需。
 */
@Configuration
@EnableConfigurationProperties(IdProperties.class)
public class IdGeneratorConfig {

    @Bean
    @ConditionalOnMissingBean
    public IdGeneratorHolder idGeneratorHolder(IdProperties props, DataSource dataSource) {
        IdGeneratorHolder holder = new IdGeneratorHolder();
        switch (props.getStrategy().toLowerCase()) {
            case "segment" -> {
                JdbcTemplate jt = new JdbcTemplate(dataSource);
                holder.setDefault(new SegmentIdStrategy(jt, props.getSegmentStep()));
            }
            case "identity" -> holder.setDefault(new IdentityIdStrategy());
            case "snowflake" -> {
                SnowflakeIdStrategy sf = props.getNodeId() != null
                        ? new SnowflakeIdStrategy(props.getNodeId())
                        : new SnowflakeIdStrategy();
                holder.setDefault(sf);
            }
            default -> throw new IllegalArgumentException("unsupported id strategy: " + props.getStrategy());
        }
        return holder;
    }
}
