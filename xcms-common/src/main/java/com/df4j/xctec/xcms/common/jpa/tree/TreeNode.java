package com.df4j.xctec.xcms.common.jpa.tree;

/**
 * 树形节点接口：所有需要级联/树形结构的实体实现此接口。
 * path 不含自身 id；codePath 含自身 id。
 */
public interface TreeNode {

    Long getId();

    Long getParentId();

    /** 祖先 id 路径（不含自身），以 '/' 分隔，根节点为 null 或空串 */
    String getPath();

    void setPath(String path);

    /** 含自身的编码路径（code 或 id 组成），以 '/' 分隔 */
    String getCodePath();

    void setCodePath(String codePath);
}
