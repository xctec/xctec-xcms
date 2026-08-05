package com.df4j.xctec.xcms.common.jpa.service;

import com.df4j.xctec.xcms.common.jpa.converter.ModelConverter;
import com.df4j.xctec.xcms.common.jpa.dto.BaseDto;
import com.df4j.xctec.xcms.common.jpa.entity.BaseEntity;
import com.df4j.xctec.xcms.common.jpa.form.BaseForm;
import com.df4j.xctec.xcms.common.jpa.repository.BaseRepository;
import com.df4j.xctec.xcms.core.exception.BizException;
import com.df4j.xctec.xcms.core.vo.PageQuery;
import com.df4j.xctec.xcms.core.vo.PageVo;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.JPQLQueryFactory;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;


@Getter
public abstract class BaseService<E extends BaseEntity,
        Q extends EntityPathBase<E>,
        D extends BaseDto,
        F extends BaseForm,
        QP extends PageQuery,
        C extends ModelConverter<E, D, F>,
        R extends BaseRepository<E>> {

    @Setter(onMethod_ = {@Autowired})
    private R repository;

    @Setter(onMethod_ = {@Autowired})
    private JPQLQueryFactory queryFactory;

    @Setter(onMethod_ = {@Autowired})
    private C converter;

    public abstract Q getQ();

    public abstract NumberPath<Long> getIdPath();

    public abstract BooleanBuilder getWhere(QP params);

    public OrderSpecifier<?>[] getOrders(QP params) {
        return new OrderSpecifier<?>[0];
    }

    public List<D> list(QP params) {
        BooleanBuilder where = this.getWhere(params);
        return this.getQueryFactory()
                .selectFrom(this.getQ())
                .where(where)
                .orderBy(this.getOrders(params))
                .fetch()
                .stream()
                .map(x -> this.getConverter().toDto(x))
                .toList();
    }

    public PageVo<D> page(QP params) {

        BooleanBuilder where = this.getWhere(params);

        var q = this.getQueryFactory()
                .from(this.getQ())
                .where(where)
                .orderBy(this.getOrders(params));

        Long total = q.select(Expressions.ONE.count())
                .limit(-1)
                .offset(-1)
                .fetchOne();

        if (total == null) {
            return PageVo.empty();
        }
        List<D> list = q.select(this.getQ())
                .limit(params.getPageSize())
                .offset(params.offset())
                .fetch()
                .stream()
                .map(x -> this.getConverter().toDto(x))
                .toList();
        return PageVo.of(params.getPageNo(), params.getPageSize(), total, list);
    }

    public F editItem(Long id) {
        return this.getRepository()
                .findById(id)
                .map(x -> this.getConverter().toForm(x))
                .orElse(null);
    }

    public F create(F form) {
        E entity = this.getConverter().toEntity(form);
        E saved = this.getRepository().save(entity);
        return this.getConverter().toForm(saved);
    }

    public F edit(F form) {
        Long id = form.getId();
        if (id == null) {
            throw BizException.of("-1", "未传入id, 无法修改");
        }
        E entity = this.getRepository()
                .findById(id)
                .orElseThrow(() -> BizException.of("-2", "找不到制定id的记录"));
        this.getConverter().setEntity(form, entity);
        E saved = this.getRepository().save(entity);
        return this.getConverter().toForm(saved);
    }

    public long del(Long id) {
        return this.getQueryFactory()
                .delete(this.getQ())
                .where(this.getIdPath().eq(id))
                .execute();
    }

    public long delAll(List<Long> ids) {
        return this.getQueryFactory()
                .delete(this.getQ())
                .where(this.getIdPath().in(ids))
                .execute();
    }
}
