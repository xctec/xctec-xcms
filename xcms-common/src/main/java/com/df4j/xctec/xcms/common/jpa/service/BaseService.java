package com.df4j.xctec.xcms.common.jpa.service;

import com.df4j.xctec.xcms.common.jpa.converter.ModelConverter;
import com.df4j.xctec.xcms.common.jpa.dto.BaseDto;
import com.df4j.xctec.xcms.common.jpa.entity.BaseEntity;
import com.df4j.xctec.xcms.common.jpa.entity.TenantScoped;
import com.df4j.xctec.xcms.common.jpa.form.BaseForm;
import com.df4j.xctec.xcms.common.jpa.repository.BaseRepository;
import com.df4j.xctec.xcms.core.context.tenant.TenantContextUtils;
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

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;


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

    /**
     * 落库前的钩子，子类可覆写以对实体做统一处理（如密码加密）。
     * 默认空实现，仅对覆写的实体生效，不影响其他实体。
     */
    protected void onBeforePersist(E entity) {
    }

    public abstract Q getQ();

    public abstract NumberPath<Long> getIdPath();

    public abstract BooleanBuilder getWhere(QP params);

    public OrderSpecifier<?>[] getOrders(QP params) {
        return new OrderSpecifier<?>[0];
    }

    /**
     * 当前实体类型是否实现 {@link TenantScoped}，需要按租户隔离。
     */
    protected boolean isTenantScoped() {
        return TenantScoped.class.isAssignableFrom(this.getQ().getType());
    }

    /**
     * 取当前请求的租户ID（来自 {@code X-Tenant-Id} 头，由 TenantContextFilter 写入）。
     */
    protected Optional<Long> currentTenantId() {
        return TenantContextUtils.tenantId();
    }

    /**
     * 在查询条件上追加租户过滤（仅对实现 {@link TenantScoped} 的实体生效）。
     */
    protected BooleanBuilder withTenantFilter(BooleanBuilder where) {
        if (isTenantScoped()) {
            currentTenantId().ifPresent(tid -> where.and(getTenantIdPath().eq(tid)));
        }
        return where;
    }

    /**
     * 校验实体属于当前租户，防止越权读写其他租户数据。非租户隔离实体直接放行。
     */
    protected void ensureSameTenant(E entity) {
        if (entity instanceof TenantScoped ts) {
            Long current = currentTenantId().orElse(null);
            if (current == null || !current.equals(ts.getTenantId())) {
                throw BizException.of("403", "无权操作非本租户的记录");
            }
        }
    }

    /**
     * 写入时把租户ID从上下文 stamp 到实体（form 不得携带 tenantId）。
     */
    protected void stampTenantId(E entity) {
        if (entity instanceof TenantScoped ts) {
            currentTenantId().ifPresent(ts::setTenantId);
        }
    }

    /**
     * 反射获取 Q 类型的 tenantId 路径（租户隔离实体必有该 public 字段）。
     */
    @SuppressWarnings("unchecked")
    protected NumberPath<Long> getTenantIdPath() {
        Q q = this.getQ();
        Class<?> qClass = q.getClass();
        Field field = TENANT_FIELD_CACHE.computeIfAbsent(qClass, k -> {
            try {
                return k.getField("tenantId");
            } catch (NoSuchFieldException e) {
                throw new IllegalStateException("租户隔离实体缺失 tenantId 字段: " + k.getName(), e);
            }
        });
        try {
            return (NumberPath<Long>) field.get(q);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("无法读取 tenantId 字段: " + qClass.getName(), e);
        }
    }

    private static final Map<Class<?>, Field> TENANT_FIELD_CACHE = new ConcurrentHashMap<>();

    public List<D> list(QP params) {
        BooleanBuilder where = withTenantFilter(this.getWhere(params));
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

        BooleanBuilder where = withTenantFilter(this.getWhere(params));

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
                .map(x -> {
                    ensureSameTenant(x);
                    return this.getConverter().toForm(x);
                })
                .orElse(null);
    }

    public F create(F form) {
        E entity = this.getConverter().toEntity(form);
        stampTenantId(entity);
        onBeforePersist(entity);
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
        ensureSameTenant(entity);
        this.getConverter().setEntity(form, entity);
        stampTenantId(entity);
        onBeforePersist(entity);
        E saved = this.getRepository().save(entity);
        return this.getConverter().toForm(saved);
    }

    public long del(Long id) {
        BooleanBuilder where = new BooleanBuilder()
                .and(this.getIdPath().eq(id));
        withTenantFilter(where);
        return this.getQueryFactory()
                .delete(this.getQ())
                .where(where)
                .execute();
    }

    public long delAll(List<Long> ids) {
        BooleanBuilder where = new BooleanBuilder()
                .and(this.getIdPath().in(ids));
        withTenantFilter(where);
        return this.getQueryFactory()
                .delete(this.getQ())
                .where(where)
                .execute();
    }
}
