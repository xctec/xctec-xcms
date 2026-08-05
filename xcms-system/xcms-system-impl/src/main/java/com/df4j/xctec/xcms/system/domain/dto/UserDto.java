package com.df4j.xctec.xcms.system.domain.dto;

import com.df4j.xctec.xcms.common.jpa.dto.BaseDto;
import lombok.Data;

@Data
public class UserDto extends BaseDto {

    private String username;

    private String nickname;

    private String email;

    private String phone;

    private String avatar;

    private Long orgUnitId;

    private String enableStatus;
}
