package com.df4j.xctec.xcms.portal.api.service;

import com.df4j.xctec.xcms.portal.api.dto.DashboardVo;
import com.df4j.xctec.xcms.portal.api.dto.MenuTreeVo;
import com.df4j.xctec.xcms.portal.api.dto.UserProfileVo;

import java.util.List;

/**
 * 门户 BFF 契约（接口）。实现位于 xcms-portal-impl，遵循接口先行原则。
 * 负责聚合 system 的菜单/用户与 auth 的主体信息，向下游前端一次性输出。
 */
public interface PortalService {

    /**
     * 当前登录用户个人中心资料。
     *
     * @return 个人资料
     */
    UserProfileVo profile();

    /**
     * 当前用户可见的菜单树（按权限码过滤）。
     *
     * @return 菜单树根节点列表
     */
    List<MenuTreeVo> menus();

    /**
     * 首页聚合：个人资料 + 菜单树 + 动态卡片。
     *
     * @return 聚合视图
     */
    DashboardVo dashboard();
}
