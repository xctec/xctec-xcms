package com.df4j.xctec.xcms.common.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;

import java.time.Instant;

@Data
@MappedSuperclass
public abstract class BaseAuditableEntity extends BaseEntity {

    @Column(name = "update_by", precision = 18, comment = "修改人ID")
    private Long updateBy;

    @Column(name = "update_time", nullable = false, comment = "修改人时间")
    private Instant updateTime;

}
