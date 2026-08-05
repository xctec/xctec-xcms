package com.df4j.xctec.xcms.system.domain.dto;

import com.df4j.xctec.xcms.common.jpa.dto.BaseTreeDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MenuDto extends BaseTreeDto<MenuDto> {

    private String menuName;

    private String menuType;

    private String routePath;

    private String component;

    private String icon;

    private String permission;

    private String visibleStatus;

    private String enableStatus;
}
