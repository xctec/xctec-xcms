package com.df4j.xctec.xcms.core.id;

/**
 * ID 生成策略抽象。
 * 接口定义于 xcms-core，实现在 xcms-base（SnowflakeIdStrategy / SegmentIdStrategy / IdentityIdStrategy）。
 */
public interface IdGenerateStrategy {

    /**
     * 由数据库产生 ID 时返回 true，此时 nextId() 不会被调用。
     * 仅 identity 策略返回 true。
     */
    default boolean databaseGenerated() {
        return false;
    }

    /**
     * 生成下一个 id。仅在 databaseGenerated() 返回 false 时调用。
     */
    Long nextId();

    /**
     * 策略名称，与配置键一致：snowflake / segment / identity。
     */
    String name();
}
