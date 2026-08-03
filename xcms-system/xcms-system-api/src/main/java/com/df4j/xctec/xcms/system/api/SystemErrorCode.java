package com.df4j.xctec.xcms.system.api;

import com.df4j.xctec.xcms.core.result.ErrorCode;

/**
 * system 模块专属错误码（租户、组织、用户、角色、权限）。
 * 公共 / 租户上下文相关的通用错误码见 {@link com.df4j.xctec.xcms.core.result.CommonErrorCode}。
 */
public enum SystemErrorCode implements ErrorCode {

    TENANT_NOT_FOUND("system.tenant.notFound", "租户不存在：{0}"),
    TENANT_NOT_MANAGEABLE("system.tenant.notManageable", "无权管理目标租户：{0}"),
    TENANT_CODE_DUPLICATED("system.tenant.codeDuplicated", "租户编码重复：{0}"),
    TENANT_NAME_DUPLICATED("system.tenant.nameDuplicated", "租户名称重复：{0}"),

    TREE_PARENT_NOT_FOUND("system.tree.parentNotFound", "父节点不存在：{0}"),
    TREE_CODE_DUPLICATED("system.tree.codeDuplicated", "同级编码重复：{0}"),

    ORG_NOT_FOUND("system.org.notFound", "组织不存在：{0}"),
    USER_NOT_FOUND("system.user.notFound", "用户不存在：{0}"),
    USER_USERNAME_DUPLICATED("system.user.usernameDuplicated", "用户名已存在：{0}"),
    ROLE_NOT_FOUND("system.role.notFound", "角色不存在：{0}"),
    ROLE_CODE_DUPLICATED("system.role.codeDuplicated", "角色编码重复：{0}"),
    PERMISSION_NOT_FOUND("system.permission.notFound", "权限不存在：{0}");

    private final String code;
    private final String messageTemplate;

    SystemErrorCode(String code, String messageTemplate) {
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
