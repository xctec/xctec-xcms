package com.df4j.xctec.xcms.auth.controller;

import com.df4j.xctec.xcms.auth.form.LoginForm;
import com.df4j.xctec.xcms.common.security.authentication.userdetails.XcmsUserDetails;
import com.df4j.xctec.xcms.common.security.token.TokenManager;
import com.df4j.xctec.xcms.core.context.tenant.TenantContextUtils;
import com.df4j.xctec.xcms.core.utils.ResultUtils;
import com.df4j.xctec.xcms.core.vo.ResultVo;
import com.df4j.xctec.xcms.core.vo.TokenVo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class LoginController {

    private final TokenManager tokenManager;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public ResultVo<TokenVo> login(@RequestBody LoginForm loginForm) {
        // 租户从上下文（请求头 X-Tenant-Id 经 TenantContextFilter 写入）获取，而非表单字段
        return TenantContextUtils.tenantId()
                .map(tenantId -> doLogin(loginForm))
                .orElseGet(() -> ResultUtils.error("-1", "缺少租户标识，请在请求头携带 X-Tenant-Id"));
    }

    private ResultVo<TokenVo> doLogin(LoginForm loginForm) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginForm.getUsername(), loginForm.getPassword())
            );
            XcmsUserDetails details = (XcmsUserDetails) authentication.getPrincipal();
            // 登录成功后写入租户上下文，供当前请求链路（如审计、数据过滤）使用
            TenantContextUtils.setTenantId(details.getTenantId());
            TokenVo tokenVo = tokenManager.generate(authentication);
            return ResultUtils.success(tokenVo);
        } catch (AuthenticationException e) {
            return ResultUtils.error("-1", "用户名或密码不正确");
        }
    }
}
