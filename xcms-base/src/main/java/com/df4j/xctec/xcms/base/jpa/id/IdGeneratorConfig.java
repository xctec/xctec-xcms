package com.df4j.xctec.xcms.base.jpa.id;

import com.df4j.xctec.xcms.core.id.IdGenerateStrategy;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 * ID 生成器自动配置：根据 xcms.id.type 选择策略。
 *
 * 注入时机关键：idHolderInitializer 是 BeanFactoryPostProcessor，
 * 在 EntityManagerFactory 创建之前执行，确保 IdGeneratorHolder 已就绪。
 */
@Configuration
@EnableConfigurationProperties(IdProperties.class)
public class IdGeneratorConfig {

    @Bean
    @ConditionalOnProperty(name = "xcms.id.type", havingValue = "snowflake", matchIfMissing = true)
    public IdGenerateStrategy snowflakeStrategy(IdProperties props) {
        Long workerId = props.getWorkerId();
        Long datacenterId = props.getDatacenterId();
        if (workerId != null && datacenterId != null) {
            return new SnowflakeIdStrategy(workerId, datacenterId);
        }
        return new SnowflakeIdStrategy();
    }

    @Bean
    @ConditionalOnProperty(name = "xcms.id.type", havingValue = "segment")
    public IdGenerateStrategy segmentStrategy(DataSource dataSource, IdProperties props) {
        JdbcTemplate jt = new JdbcTemplate(dataSource);
        return new SegmentIdStrategy(jt, props.getStep());
    }

    @Bean
    @ConditionalOnProperty(name = "xcms.id.type", havingValue = "identity")
    public IdGenerateStrategy identityStrategy() {
        return new IdentityIdStrategy();
    }

    /**
     * 早于 EntityManagerFactory 完成注入，将策略写入 IdGeneratorHolder 静态字段。
     */
    @Bean
    public static BeanFactoryPostProcessor idHolderInitializer() {
        return bf -> IdGeneratorHolder.set(bf.getBean(IdGenerateStrategy.class));
    }
}
