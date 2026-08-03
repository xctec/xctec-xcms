package com.df4j.xctec.xcms.common.jpa.id;

/**
 * ID 生成策略抽象。
 */
public interface IdGenerateStrategy {

    long nextId(Class<?> entityClass);

    /** 策略名称，与配置键一致：snowflake / segment / identity。 */
    String name();
}
