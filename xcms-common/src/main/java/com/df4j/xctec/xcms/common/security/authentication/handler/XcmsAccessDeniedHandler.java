package com.df4j.xctec.xcms.common.security.authentication.handler;

import com.df4j.xctec.xcms.core.utils.ResponseUtils;
import com.df4j.xctec.xcms.core.utils.ResultUtils;
import com.df4j.xctec.xcms.core.vo.ResultVo;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;

@Slf4j
public class XcmsAccessDeniedHandler implements AccessDeniedHandler {

    @SuppressWarnings("NullableProblems")
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        if (log.isDebugEnabled()) {
            log.debug("访问被拒绝", accessDeniedException);
        }
        ResultVo<?> resultVo = ResultUtils.error("-403", "访问被拒绝");
        ResponseUtils.writeJson(response, resultVo, HttpStatus.FORBIDDEN);
    }
}
