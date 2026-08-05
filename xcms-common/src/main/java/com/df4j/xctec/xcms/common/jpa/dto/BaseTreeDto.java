package com.df4j.xctec.xcms.common.jpa.dto;

import com.df4j.xctec.xcms.core.tree.SubTree;
import lombok.Data;

import java.util.List;

@Data
public class BaseTreeDto<T extends BaseTreeDto<T>> extends BaseDto implements SubTree<T> {
    private String nodeCode;
    private Long parentId;
    private Integer treeLevel;
    private String path;
    private String codePath;
    private Integer sortNum;
    private List<T> children;
}
