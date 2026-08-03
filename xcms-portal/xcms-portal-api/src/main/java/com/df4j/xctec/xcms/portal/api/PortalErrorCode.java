package com.df4j.xctec.xcms.portal.api;

import com.df4j.xctec.xcms.core.result.ErrorCode;

/**
 * portal 模块专属错误码（BFF：首页 / 菜单树 / 个人中心）。
 * 公共 / 鉴权 / 租户上下文相关错误码见 {@link com.df4j.xctec.xcms.core.result.CommonErrorCode}
 * 与 {@code com.df4j.xctec.xcms.auth.api.AuthErrorCode}。
 */
public enum PortalErrorCode implements ErrorCode {

    MENU_NOT_FOUND("portal.menu.notFound", "菜单不存在：{0}"),
    MENU_BUILD_FAILED("portal.menu.buildFailed", "菜单树构建失败"),
    PROFILE_NOT_FOUND("portal.profile.notFound", "个人资料不存在"),
    DASHBOARD_BUILD_FAILED("portal.dashboard.buildFailed", "首页聚合失败");

    private final String code;
    private final String messageTemplate;

    PortalErrorCode(String code, String messageTemplate) {
        this.code = code;
        this.messageTemplate = messageTemplate;
    }

    @Override
    public String code() {
        return code;
    }

    @Override
    public String messageTemplate() {
        return messageTemplate;
    }
}
