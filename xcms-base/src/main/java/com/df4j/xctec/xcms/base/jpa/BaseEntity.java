package com.df4j.xctec.xcms.base.jpa;

import com.df4j.xctec.xcms.base.jpa.id.XcmsIdGenerator;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import jakarta.persistence.Transient;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 所有持久化实体的基类。主键为 Long（雪花/号段/自增）。
 *
 * 实现 Persistable 并配合 @Transient isNew 标记，
 * 避免预生成 ID 场景下 save() 前的多余 SELECT。
 */
@MappedSuperclass
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity implements Persistable<Long> {

    @Id
    @GeneratedValue(generator = "xcms-id")
    @GenericGenerator(name = "xcms-id", type = XcmsIdGenerator.class)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @CreatedBy
    @Column(name = "created_by", updatable = false)
    private Long createdBy;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedBy
    @Column(name = "updated_by")
    private Long updatedBy;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted", nullable = false)
    private Boolean deleted = Boolean.FALSE;

    @Version
    @Column(name = "version")
    private Integer version;

    /** 配合预生成 ID，避免 save 前的多余 select */
    @Transient
    private transient boolean isNew = true;

    @Override
    public boolean isNew() {
        return isNew;
    }

    @PostPersist
    @PostLoad
    void markNotNew() {
        this.isNew = false;
    }
}
