package com.df4j.xctec.xcms.auth.api.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 登录请求参数。
 */
@Data
public class LoginRequest implements Serializable {

    private String username;

    private String password;
}
