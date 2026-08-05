package com.df4j.xctec.xcms.common.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;

import java.time.Instant;

@Data
@MappedSuperclass
public abstract class BaseEntity {

    @Column(name = "create_by", precision = 18, nullable = false, updatable = false, comment = "创建人ID")
    private Long createBy;

    @Column(name = "create_time", nullable = false, updatable = false, comment = "创建人时间")
    private Instant createTime;

    // 让子类标注使用的ID生成策略
    public abstract Long getId();

    public abstract void setId(Long id);

}
