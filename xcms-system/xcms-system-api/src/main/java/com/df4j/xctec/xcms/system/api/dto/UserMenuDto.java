package com.df4j.xctec.xcms.system.api.dto;

import com.df4j.xctec.xcms.common.jpa.dto.BaseTreeDto;
import lombok.Data;

/**
 * 用户菜单只读视图，独立于 system 内部 {@code MenuDto}（领域 DTO）。
 * 仅暴露前端渲染菜单树所需字段，不含任何写入/管理属性。
 */
@Data
public class UserMenuDto extends BaseTreeDto<UserMenuDto> {

    private String menuName;

    private String menuType;

    private String routePath;

    private String component;

    private String icon;

    private String permission;

    private String visibleStatus;

    private String enableStatus;
}
