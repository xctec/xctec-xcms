package com.df4j.xctec.xcms.auth.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

/**
 * 令牌刷新入参。
 */
@Data
public class RefreshDto implements Serializable {

    @NotBlank(message = "refreshToken 不能为空")
    private String refreshToken;
}
