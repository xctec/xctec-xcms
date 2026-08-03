package com.df4j.xctec.xcms.auth.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * 登录入参。
 */
@Data
public class LoginDto implements Serializable {

    @NotBlank(message = "用户名不能为空")
    @Size(max = 64, message = "用户名最长 64 位")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;

    /** 目标租户 id，运营端可指定登录到某租户；为空则默认用户主租户 */
    private Long tenantId;
}
