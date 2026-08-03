package com.df4j.xctec.xcms.base.jpa.tree;

import com.df4j.xctec.xcms.core.exception.BizException;
import com.df4j.xctec.xcms.core.result.CommonErrorCode;
import com.df4j.xctec.xcms.core.tree.TreeNode;
import com.df4j.xctec.xcms.core.tree.TreePath;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;

/**
 * 树形结构的统一维护：处理 path（不含自身）与 codePath（含自身）的写入与校验。
 * 树逻辑不放在实体基类，抽成服务，任何 TreeNode 均可复用。
 *
 * 由于 path 不含自身 id，仅依赖父节点 id（父节点已入库，id 必定存在），
 * 故新增节点可在获得父节点后一次性写入，无需二次回填。
 */
@Service
public class TreeService {

    /**
     * 新增节点时计算路径，parent 为 null 表示根。
     */
    public <T extends TreeNode> void fillPath(T node, T parent) {
        if (parent == null) {
            node.setParentId(TreeNode.ROOT_PARENT_ID);
            node.setTreePath(new TreePath(
                    TreeNode.SEPARATOR + TreeNode.ROOT_PARENT_ID + TreeNode.SEPARATOR,
                    TreeNode.SEPARATOR + node.getNodeCode() + TreeNode.SEPARATOR,
                    1));
        } else {
            node.setParentId(parent.getId());
            node.setTreePath(new TreePath(
                    parent.descendantPathPrefix(),
                    parent.getTreePath().getCodePath() + node.getNodeCode() + TreeNode.SEPARATOR,
                    parent.getTreePath().getLevel() + 1));
        }
    }

    /**
     * 校验迁移合法性：目标不能是自身或子孙。
     */
    public <T extends TreeNode> void validateMove(T node, T newParent) {
        if (newParent == null) {
            return;
        }
        if (node.getId() != null && node.getId().equals(newParent.getId())) {
            throw new BizException(CommonErrorCode.TREE_MOVE_TO_SELF);
        }
        if (newParent.getTreePath().getPath().startsWith(node.descendantPathPrefix())) {
            throw new BizException(CommonErrorCode.TREE_MOVE_TO_DESCENDANT);
        }
    }

    /**
     * 返回祖先 id 列表（不含自身、不含根 0）。
     */
    public List<Long> ancestorIds(TreeNode node) {
        return Arrays.stream(node.getTreePath().getPath().split(TreeNode.SEPARATOR))
                .filter(StringUtils::hasText)
                .map(Long::valueOf)
                .filter(id -> !TreeNode.ROOT_PARENT_ID.equals(id))
                .toList();
    }
}
