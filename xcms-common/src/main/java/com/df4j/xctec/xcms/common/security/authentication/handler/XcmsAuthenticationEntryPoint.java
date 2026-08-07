package com.df4j.xctec.xcms.common.security.authentication.handler;

import com.df4j.xctec.xcms.core.utils.ResponseUtils;
import com.df4j.xctec.xcms.core.utils.ResultUtils;
import com.df4j.xctec.xcms.core.vo.ResultVo;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

@Slf4j
public class XcmsAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @SuppressWarnings("NullableProblems")
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        HttpStatus httpStatus = HttpStatus.OK;
        String errorNo = "-1";
        String errorMsg = "用户登录名或密码不正确";
        if (log.isDebugEnabled()) {
            log.debug("认证出现异常，返回前端发起异常流程", authException);
        }
        ResultVo<?> resultVo = ResultUtils.error(errorNo, errorMsg);
        ResponseUtils.writeJson(response, resultVo, httpStatus);
    }
}
