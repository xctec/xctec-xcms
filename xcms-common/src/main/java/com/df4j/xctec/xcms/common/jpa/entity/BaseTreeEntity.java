package com.df4j.xctec.xcms.common.jpa.entity;

import com.df4j.xctec.xcms.core.tree.TreeNode;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;

@Data
@MappedSuperclass
public abstract class BaseTreeEntity extends BaseAuditableEntity implements TreeNode {

    @Column(name = "node_code", length = 200, nullable = false, comment = "节点代码")
    private String nodeCode;

    @Column(name = "parent_id", precision = 18, nullable = false, comment = "上级ID,根为0")
    private Long parentId;

    @Column(name = "tree_level", precision = 8, nullable = false, comment = "树形层级")
    private Integer treeLevel;

    @Column(name = "path", length = 200, nullable = false, comment = "祖先ID拼接的路径，不包含自身，例如：/0/12/")
    private String path;

    @Column(name = "code_path", length = 200, nullable = false, comment = "祖先codePath与自身code拼接的路径，例如：/0/12/")
    private String codePath;

    @Column(name = "sort_num", precision = 8, nullable = false, comment = "排序值，大的靠前")
    private Integer sortNum;

}
