package com.df4j.xctec.xcms.portal.api.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 菜单树节点（前端侧边栏 / 权限树展示）。
 */
@Data
public class MenuTreeVo implements Serializable {

    private Long id;
    private Long parentId;
    /** 菜单编码，如 system:tenant */
    private String code;
    /** 菜单名称 */
    private String name;
    /** 路由 / 资源标识，可空（目录节点） */
    private String route;
    /** 图标，可空 */
    private String icon;
    /** 排序号 */
    private Integer sortNo;
    /** 类型：1 目录 2 菜单 3 按钮 */
    private Integer type;

    private List<MenuTreeVo> children = new ArrayList<>();
}
