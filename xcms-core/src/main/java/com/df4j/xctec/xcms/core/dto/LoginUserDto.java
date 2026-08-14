package com.df4j.xctec.xcms.core.dto;


import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
@Builder
public class LoginUserDto {
    private Long userId;
    private String username;
    private String nickname;
    private Long tenantId;
    private String avatar;
    private String mobile;
    private String email;
    private String encType;
    private String password;
    private Set<String> roleCodes;
    private List<String> roleNames;
    private String orgUnitName;
    private String enableStatus;
}
