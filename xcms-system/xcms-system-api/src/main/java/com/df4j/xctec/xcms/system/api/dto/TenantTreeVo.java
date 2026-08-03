package com.df4j.xctec.xcms.system.api.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 租户树节点（嵌套结构，供前端树形展示）。
 */
@Data
public class TenantTreeVo implements Serializable {

    private Long id;
    private Long parentId;
    private String code;
    private String name;
    private Integer level;
    private Integer status;

    /** 子节点 */
    private List<TenantTreeVo> children = new ArrayList<>();
}
