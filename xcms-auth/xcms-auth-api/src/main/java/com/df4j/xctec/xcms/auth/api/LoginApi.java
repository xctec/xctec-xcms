package com.df4j.xctec.xcms.auth.api;

import com.df4j.xctec.xcms.auth.api.dto.LoginRequest;
import com.df4j.xctec.xcms.auth.api.dto.RefreshRequest;
import com.df4j.xctec.xcms.core.vo.ResultVo;
import com.df4j.xctec.xcms.core.vo.TokenVo;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * 认证接口契约（定义在 auth-api，实现在 auth-impl）。
 * <p>
 * 后续拆分为独立微服务时，本接口即可作为 Feign/OpenAPI 契约直接使用。
 */
public interface LoginApi {

    @PostMapping("/auth/login")
    ResultVo<TokenVo> login(@RequestBody LoginRequest request);

    @PostMapping("/auth/refresh")
    ResultVo<TokenVo> refresh(@RequestBody RefreshRequest request);

    @PostMapping("/auth/logout")
    ResultVo<Void> logout(@RequestHeader(value = "Authorization", required = false) String authorization);
}
