package com.df4j.xctec.xcms.core.tree;

/**
 * 树形节点接口：所有需要级联/树形结构的实体实现此接口。
 *
 * path 不含自身 id；codePath 含自身 code。
 * 因 path 不含自身 id，仅依赖父节点 id（父节点已入库，id 必定存在），
 * 故无论采用雪花 / 号段 / 数据库自增哪种 ID 策略，树路径均可在 persist 前一次性计算并写入。
 */
public interface TreeNode {

    Long ROOT_PARENT_ID = 0L;
    String SEPARATOR = "/";

    Long getId();

    Long getParentId();

    void setParentId(Long parentId);

    /**
     * 用于拼 codePath 的业务编码。
     */
    String getNodeCode();

    TreePath getTreePath();

    void setTreePath(TreePath treePath);

    default boolean isRoot() {
        return ROOT_PARENT_ID.equals(getParentId());
    }

    /**
     * 下钻前缀：所有子孙的 path 均以此开头。
     * path 不含自身 id，故拼上自己的 id。
     */
    default String descendantPathPrefix() {
        return getTreePath().getPath() + getId() + SEPARATOR;
    }
}
