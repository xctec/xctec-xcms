package com.df4j.xctec.xcms.common.jpa.service;

import com.df4j.xctec.xcms.common.jpa.converter.ModelConverter;
import com.df4j.xctec.xcms.common.jpa.dto.BaseDto;
import com.df4j.xctec.xcms.common.jpa.dto.BaseTreeDto;
import com.df4j.xctec.xcms.common.jpa.entity.BaseTreeEntity;
import com.df4j.xctec.xcms.common.jpa.entity.TenantScoped;
import com.df4j.xctec.xcms.common.jpa.form.BaseTreeForm;
import com.df4j.xctec.xcms.common.jpa.repository.BaseRepository;
import com.df4j.xctec.xcms.core.exception.BizException;
import com.df4j.xctec.xcms.core.vo.PageQuery;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.StringPath;

import java.util.List;

public abstract class BaseTreeService<E extends BaseTreeEntity,
        Q extends EntityPathBase<E>,
        D extends BaseTreeDto<D>,
        F extends BaseTreeForm,
        QP extends PageQuery,
        C extends ModelConverter<E, D, F>,
        R extends BaseRepository<E>>
        extends BaseService<E, Q, D, F, QP, C, R> {


    public abstract StringPath getCodePath();


    public List<D> tree(QP params) {
        List<D> list = this.list(params);
        // 所有id
        List<Long> ids = list.stream()
                .map(BaseDto::getId)
                .toList();
        // 如果节点的parentId不在上面的集合里面，则节点为跟节点
        List<Long> rootIds = list.stream()
                .filter(x -> !ids.contains(x.getParentId()))
                .map(BaseDto::getId)
                .toList();
        return rootIds.stream()
                .flatMap(x -> buildSubTree(x, list).stream())
                .toList();
    }

    private List<D> buildSubTree(Long id, List<D> list) {
        return list.stream()
                .filter(x -> x.getParentId().equals(id))
                .peek(x -> x.setChildren(buildSubTree(x.getId(), list)))
                .toList();
    }

    @Override
    public F create(F form) {
        Long parentId = form.getParentId();
        if (parentId == null) {
            throw BizException.of("-1", "上级id不能为空");
        }
        if (form.getNodeCode().contains("/")) {
            throw BizException.of("-2", "节点代码中不能包含“/”");
        }
        E entity = this.getConverter().toEntity(form);
        if (0L == parentId) {
            entity.setTreeLevel(1);
            entity.setPath("/0/");
            entity.setCodePath(String.format("/%s/", entity.getNodeCode()));
        } else {
            E parent = this.getRepository()
                    .findById(parentId)
                    .orElseThrow(
                            () -> BizException.of("-1", "找不到指定的上级节点")
                    );
            // 上级节点必须属于当前租户，防止跨租户挂载
            ensureSameTenant(parent);
            entity.setTreeLevel(parent.getTreeLevel() + 1);
            entity.setPath(parent.getPath() + parent.getId() + "/");
            entity.setCodePath(parent.getCodePath() + entity.getNodeCode() + "/");
        }
        // 写入时把租户ID从上下文 stamp 到实体（form 不得携带 tenantId）
        stampTenantId(entity);
        onBeforePersist(entity);
        E saved = this.getRepository().save(entity);
        return this.getConverter().toForm(saved);
    }

    @Override
    public F edit(F form) {
        // todo
        // 需要考虑修改父节点，也就是移动子树的情况
        return super.edit(form);
    }

    @Override
    public long del(Long id) {
        E entity = this.getRepository()
                .findById(id)
                .orElseThrow(() -> BizException.of("-1", "找不到待删除的节点"));
        ensureSameTenant(entity);
        // 使用code_path一次性删除，并追加租户过滤
        BooleanBuilder where = new BooleanBuilder()
                .and(this.getCodePath().contains(entity.getCodePath()));
        withTenantFilter(where);
        return this.getQueryFactory()
                .delete(this.getQ())
                .where(where)
                .execute();
    }

    @Override
    public long delAll(List<Long> ids) {
        List<E> entities = this.getRepository()
                .findAllById(ids);
        if (entities.isEmpty()) {
            throw BizException.of("-1", "找不到待删除的记录");
        }
        // 使用code_path一次性删除，并追加租户过滤
        BooleanBuilder where = new BooleanBuilder();
        for (E entity : entities) {
            ensureSameTenant(entity);
            where.or(this.getCodePath().contains(entity.getCodePath()));
        }
        withTenantFilter(where);
        return this.getQueryFactory()
                .delete(this.getQ())
                .where(where)
                .execute();
    }
}
