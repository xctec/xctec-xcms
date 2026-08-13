package com.df4j.xctec.xcms.auth.controller;

import com.df4j.xctec.xcms.auth.api.LoginApi;
import com.df4j.xctec.xcms.auth.api.dto.LoginRequest;
import com.df4j.xctec.xcms.auth.api.dto.RefreshRequest;
import com.df4j.xctec.xcms.common.security.authentication.userdetails.XcmsUserDetails;
import com.df4j.xctec.xcms.common.security.token.TokenManager;
import com.df4j.xctec.xcms.core.context.tenant.TenantContextUtils;
import com.df4j.xctec.xcms.core.context.token.TokenContextUtils;
import com.df4j.xctec.xcms.core.utils.ResultUtils;
import com.df4j.xctec.xcms.core.vo.ResultVo;
import com.df4j.xctec.xcms.core.vo.TokenVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 登录接口实现（auth-impl）。
 * <p>
 * 租户标识从请求头 {@code X-Tenant-Id} 经 {@code TenantContextFilter} 写入 {@link TenantContextUtils}，
 * 不在登录表单中传递，由 common 中的 {@code UserDetailsServiceImpl} 内部从上下文获取。
 */
@RestController
public class LoginController implements LoginApi {

    private final AuthenticationManager authenticationManager;
    private final TokenManager tokenManager;

    @Autowired
    public LoginController(AuthenticationManager authenticationManager, TokenManager tokenManager) {
        this.authenticationManager = authenticationManager;
        this.tokenManager = tokenManager;
    }

    @Override
    @PostMapping("/auth/login")
    public ResultVo<TokenVo> login(@RequestBody LoginRequest request) {
        return TenantContextUtils.tenantId()
                .map(tenantId -> doLogin(request))
                .orElseGet(() -> ResultUtils.error("-1", "缺少租户标识，请在请求头携带 X-Tenant-Id"));
    }

    private ResultVo<TokenVo> doLogin(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        XcmsUserDetails details = (XcmsUserDetails) authentication.getPrincipal();
        TokenVo tokenVo = tokenManager.generate(authentication);
        return ResultUtils.success(tokenVo);
    }

    @Override
    @PostMapping("/auth/refresh")
    public ResultVo<TokenVo> refresh(@RequestBody RefreshRequest request) {
        String refreshToken = request.getRefreshToken();
        if (refreshToken == null || refreshToken.isBlank()) {
            return ResultUtils.error("-1", "缺少刷新令牌");
        }
        if (!tokenManager.validateRefreshToken(refreshToken)) {
            return ResultUtils.error("401", "刷新令牌无效或已过期");
        }
        TokenVo tokenVo = tokenManager.refreshToken(refreshToken);
        return ResultUtils.success(tokenVo);
    }

    @Override
    @PostMapping("/auth/logout")
    public ResultVo<Void> logout(String authorization) {
        // 访问令牌由 TokenContextFilter 从请求头写入上下文，从上下文获取
        return TokenContextUtils.token()
                .map(ctxToken -> {
                    tokenManager.invalid(ctxToken);
                    return ResultUtils.<Void>success();
                })
                .orElseGet(() -> ResultUtils.error("-1", "缺少访问令牌，请先登录"));
    }
}
