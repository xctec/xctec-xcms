package com.df4j.xctec.xcms.common.security.authentication.filter;

import com.df4j.xctec.xcms.common.security.authentication.token.XcmsBearerTokenAuthenticationToken;
import com.df4j.xctec.xcms.common.security.exception.InvalidTokenAuthenticationException;
import com.df4j.xctec.xcms.core.context.token.TokenContextUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XcmsBearerTokenAuthenticationFilter extends OncePerRequestFilter {


    private AuthenticationManager authenticationManager;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Optional<String> optionalToken = TokenContextUtils.token();
        if (optionalToken.isPresent()) {
            Authentication authenticationRequest = new XcmsBearerTokenAuthenticationToken(optionalToken.get());
            // 委托处理认证
            try {
                Authentication authentication = this.authenticationManager.authenticate(authenticationRequest);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (AuthenticationException e) {
                SecurityContextHolder.clearContext();
                throw e;
            }
        }
        // 继续执行过滤器链条
        filterChain.doFilter(request, response);
    }
}
