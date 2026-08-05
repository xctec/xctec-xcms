package com.df4j.xctec.xcms.common.jpa.repository;

import com.df4j.xctec.xcms.common.jpa.entity.BaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.ListQuerydslPredicateExecutor;

public interface BaseRepository<T extends BaseEntity>
        extends JpaRepository<T, Long>,
        ListQuerydslPredicateExecutor<Long> {
}
