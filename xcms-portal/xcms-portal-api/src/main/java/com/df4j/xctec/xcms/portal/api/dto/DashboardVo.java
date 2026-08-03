package com.df4j.xctec.xcms.portal.api.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 首页聚合视图（BFF 一次性返回，减少前端串行请求）。
 */
@Data
public class DashboardVo implements Serializable {

    /** 当前用户资料 */
    private UserProfileVo profile;
    /** 菜单树 */
    private List<MenuTreeVo> menus;
    /** 待办 / 统计等动态卡片，key 为卡片标识 */
    private Map<String, Object> widgets;
}
