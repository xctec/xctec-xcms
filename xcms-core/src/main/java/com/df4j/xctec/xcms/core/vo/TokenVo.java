package com.df4j.xctec.xcms.core.vo;

import lombok.Builder;
import lombok.Data;

/**
 * 认证令牌响应对象
 *
 * @author Ray.Hao
 * @since 0.0.1
 */
@Data
@Builder
public class TokenVo {

    private String tokenType;

    private String accessToken;

    private String refreshToken;

    private Integer expiresIn;

}
