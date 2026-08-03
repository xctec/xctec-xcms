package com.df4j.xctec.xcms.auth.api.dto;

import com.df4j.xctec.xcms.core.security.AuthPrincipal;
import com.df4j.xctec.xcms.core.security.TokenPair;
import lombok.Data;

import java.io.Serializable;

/**
 * 登录结果视图：令牌对 + 主体快照（前端按需使用，避免二次拉取）。
 */
@Data
public class LoginVo implements Serializable {

    private TokenPair token;
    private AuthPrincipal principal;
}
