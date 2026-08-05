package com.df4j.xctec.xcms.core.tree;

public interface TreeNode {

    void setParentId(Long parentId);

    Long getParentId();

    void setNodeCode(String nodeCode);

    Integer getTreeLevel();

    void setTreeLevel(Integer treeLevel);

    String getNodeCode();

    void setPath(String path);

    String getPath();

    void setCodePath(String codePath);

    String getCodePath();

    void setSortNum(Integer sortNum);

    Integer getSortNum();

}
