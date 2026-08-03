package com.df4j.xctec.xcms.system.api.dto;

import com.df4j.xctec.xcms.core.result.PageQuery;
import lombok.Data;

import java.io.Serializable;

/**
 * 租户视图对象（分页/详情返回）。
 * path / codePath / level 由 TreePath 展平而来。
 */
@Data
public class TenantVo implements Serializable {

    private Long id;
    private Long parentId;
    private String code;
    private String name;

    /** 祖先 id 路径，以 '/' 包围，如 /0/1/1001/ */
    private String path;
    /** 祖先 code 路径，以 '/' 包围，如 /HQ/EAST/SH/ */
    private String codePath;
    /** 层级，根为 1 */
    private Integer level;

    private Integer sortNo;
    private Integer status;

    private Long createdAt;
    private Long updatedAt;
}
