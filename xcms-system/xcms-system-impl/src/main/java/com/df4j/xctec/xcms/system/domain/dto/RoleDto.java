package com.df4j.xctec.xcms.system.domain.dto;

import com.df4j.xctec.xcms.common.jpa.dto.BaseDto;
import lombok.Data;

@Data
public class RoleDto extends BaseDto {

    private String roleCode;

    private String roleName;

    private String roleDesc;

    private String enableStatus;

}
