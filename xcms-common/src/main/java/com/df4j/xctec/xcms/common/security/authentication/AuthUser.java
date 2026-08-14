package com.df4j.xctec.xcms.common.security.authentication;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 认证所需用户只读视图（端口契约）。
 * <p>
 * 由数据提供方（如 system 模块）实现 {@link AuthUserProvider} 时构造，
 * 仅承载认证与 {@code XcmsUserDetails} 展示所需的字段，不含任何业务行为或持久化注解。
 * 该类型与具体业务实体解耦，使认证模块无需依赖业务实现层。
 * </p>
 */
@Data
public class AuthUser implements Serializable {

    private Long userId;
    private String username;
    private String password;
    private String nickname;
    private Long tenantId;
    private String avatar;
    private String mobile;
    private String email;
    private String enableStatus;
    private List<String> roleNames;
    private String orgUnitName;
}
