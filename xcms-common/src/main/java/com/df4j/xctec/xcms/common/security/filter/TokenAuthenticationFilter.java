package com.df4j.xctec.xcms.common.security.filter;

import com.df4j.xctec.xcms.common.security.constants.SecurityConstants;
import com.df4j.xctec.xcms.common.security.token.TokenManager;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private final TokenManager tokenManager;

    public TokenAuthenticationFilter(TokenManager tokenManager) {
        this.tokenManager = tokenManager;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = getTokenFromHeader(request);
        if (StringUtils.hasText(token)) {
            try {
                // 校验token 并放入
                boolean valid = tokenManager.validate(token);
                if (!valid) {
                    //
                    throw new InsufficientAuthenticationException("Token无效或过期!");
                }
                Authentication authentication = tokenManager.parse(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (Exception e) {
                AuthenticationException ex = null;
                if (e instanceof AuthenticationException) {
                    ex = (AuthenticationException) e;
                } else {
                    ex = new InsufficientAuthenticationException("解析Authentication失败", e);
                }
                throw ex;
            } finally {
                SecurityContextHolder.clearContext();
            }
        }
        filterChain.doFilter(request, response);
    }

    private String getTokenFromHeader(HttpServletRequest request) {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(authorization)) {
            if (authorization.startsWith(SecurityConstants.BEARER_PREFIX)) {
                return authorization.substring(SecurityConstants.BEARER_PREFIX.length());
            }
        }
        return null;
    }
}
