package com.df4j.xctec.xcms.common.jpa.id;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * ID 生成器配置属性，绑定 xcms.id.*。
 */
@ConfigurationProperties(prefix = "xcms.id")
public class IdProperties {

    /** 默认策略：snowflake / segment / identity */
    private String strategy = "snowflake";

    /** 号段步长（仅 segment 策略使用） */
    private int segmentStep = 1000;

    /** 雪花节点号，缺省自动计算 */
    private Long nodeId;

    public String getStrategy() {
        return strategy;
    }

    public void setStrategy(String strategy) {
        this.strategy = strategy;
    }

    public int getSegmentStep() {
        return segmentStep;
    }

    public void setSegmentStep(int segmentStep) {
        this.segmentStep = segmentStep;
    }

    public Long getNodeId() {
        return nodeId;
    }

    public void setNodeId(Long nodeId) {
        this.nodeId = nodeId;
    }
}
