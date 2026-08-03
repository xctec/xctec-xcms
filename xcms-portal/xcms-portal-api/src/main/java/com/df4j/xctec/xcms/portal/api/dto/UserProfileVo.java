package com.df4j.xctec.xcms.portal.api.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 个人中心视图：当前登录用户基础资料 + 所属租户 + 角色 + 权限码。
 */
@Data
public class UserProfileVo implements Serializable {

    private Long userId;
    private String username;
    private String nickname;
    private String avatar;

    private Long tenantId;
    private String tenantName;
    private String tenantPath;

    private List<Long> roleIds;
    private List<String> permissions;
}
