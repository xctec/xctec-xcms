package com.df4j.xctec.xcms.common.jpa.tree;

import com.df4j.xctec.xcms.common.core.BizException;
import com.df4j.xctec.xcms.common.core.CommonErrorCode;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.function.Function;

/**
 * 树形结构的统一维护：处理 path（不含自身）与 codePath（含自身）的写入与迁移。
 * 由于 path 不含自身 id，新增节点可在获得 id 后一次性写入，无需二次回填。
 *
 * @param <T> 树节点实体类型
 */
public class TreeService<T extends TreeNode, R> {

    private final R repository;
    private final Function<Long, Optional<T>> findById;
    private final Function<T, T> save;

    public TreeService(R repository,
                       Function<Long, Optional<T>> findById,
                       Function<T, T> save) {
        this.repository = repository;
        this.findById = findById;
        this.save = save;
    }

    /**
     * 创建节点：写入父 path + 自身 id（path 不含自身），codePath 含自身。
     */
    @Transactional
    public T create(T node, Long parentId, String selfCode) {
        if (parentId == null || parentId == 0) {
            node.setPath(null);
            node.setCodePath(selfCode);
            return save.apply(node);
        }
        T parent = findById.apply(parentId)
                .orElseThrow(() -> new BizException(CommonErrorCode.COMMON_NOT_FOUND, "parent " + parentId));
        node.setPath(TreePath.childPath(parent.getPath(), parent.getId()));
        node.setCodePath(TreePath.childCodePath(parent.getCodePath(), selfCode));
        return save.apply(node);
    }

    /**
     * 迁移子树：仅重算受影响节点的 path / codePath。
     * 防御环路：目标不能是自身或子孙。
     */
    @Transactional
    public T migrate(T node, Long newParentId, String selfCode) {
        String newParentPath;
        String newParentCodePath;
        if (newParentId == null || newParentId == 0) {
            newParentPath = null;
            newParentCodePath = "";
        } else {
            T newParent = findById.apply(newParentId)
                    .orElseThrow(() -> new BizException(CommonErrorCode.COMMON_NOT_FOUND, "parent " + newParentId));
            if (node.getId() != null
                    && (node.getId().equals(newParentId)
                        || TreePath.isDescendant(node.getPath(), newParent.getPath()))) {
                throw new BizException(CommonErrorCode.TENANT_CYCLE);
            }
            newParentPath = newParent.getPath();
            newParentCodePath = newParent.getCodePath();
        }
        rewrite(node, newParentPath, newParentCodePath, selfCode);
        return save.apply(node);
    }

    private void rewrite(T node, String parentPath, String parentCodePath, String selfCode) {
        node.setPath(TreePath.childPath(parentPath, node.getId()));
        node.setCodePath(TreePath.childCodePath(parentCodePath, selfCode));
    }
}
