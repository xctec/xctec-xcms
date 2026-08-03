package com.df4j.xctec.xcms.base.jpa.id;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * ID 生成器配置属性，绑定 xcms.id.*。
 *
 * type: snowflake（默认） | segment | identity
 * worker-id / datacenter-id: 仅 snowflake 使用，多实例部署时必须分配不同值
 * step: 仅 segment 使用，号段步长
 */
@Data
@ConfigurationProperties(prefix = "xcms.id")
public class IdProperties {

    /** 默认策略：snowflake / segment / identity */
    private String type = "snowflake";

    /** 雪花 workerId（0-31），多实例必须不同 */
    private Long workerId;

    /** 雪花 datacenterId（0-31），多实例必须不同 */
    private Long datacenterId;

    /** 号段步长（仅 segment 策略使用） */
    private Integer step = 1000;
}
