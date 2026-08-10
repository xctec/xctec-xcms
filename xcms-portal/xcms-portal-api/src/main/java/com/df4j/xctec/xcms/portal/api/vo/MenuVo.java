package com.df4j.xctec.xcms.portal.api.vo;

import lombok.Data;

import java.util.List;

/**
 * 菜单视图对象（契约），定义在 portal-api，供 portal-impl 与前端共享。
 */
@Data
public class MenuVo {

    private Long id;

    private Long parentId;

    private String menuName;

    private String menuType;

    private String routePath;

    private String component;

    private String icon;

    private String permission;

    private String visibleStatus;

    private String enableStatus;

    private List<MenuVo> children;
}
