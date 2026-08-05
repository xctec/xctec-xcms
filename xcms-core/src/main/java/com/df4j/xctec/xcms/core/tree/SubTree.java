package com.df4j.xctec.xcms.core.tree;

import java.util.List;

public interface SubTree<T extends SubTree<T>> extends TreeNode {

    List<T> getChildren();

    void setChildren(List<T> children);
}
