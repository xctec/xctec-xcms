package com.df4j.xctec.xcms.auth.api.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 刷新令牌请求参数。
 */
@Data
public class RefreshRequest implements Serializable {

    private String refreshToken;
}
