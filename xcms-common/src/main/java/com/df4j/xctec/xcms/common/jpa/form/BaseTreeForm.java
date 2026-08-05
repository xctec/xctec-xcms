package com.df4j.xctec.xcms.common.jpa.form;

import com.df4j.xctec.xcms.core.tree.TreeNode;
import lombok.Data;

@Data
public class BaseTreeForm extends BaseForm implements TreeNode {
    private String nodeCode;
    private Long parentId;
    private Integer treeLevel;
    private String path;
    private String codePath;
    private Integer sortNum;
}
