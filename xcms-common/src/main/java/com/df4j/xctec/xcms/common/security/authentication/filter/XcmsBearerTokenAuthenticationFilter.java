package com.df4j.xctec.xcms.common.security.authentication.filter;

import com.df4j.xctec.xcms.common.security.authentication.token.XcmsBearerTokenAuthenticationToken;
import com.df4j.xctec.xcms.common.security.exception.InvalidTokenAuthenticationException;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XcmsBearerTokenAuthenticationFilter extends OncePerRequestFilter {

    private final String bearerTokenHeaderName = HttpHeaders.AUTHORIZATION;

    private static final Pattern authorizationPattern = Pattern.compile("^Bearer (?<token>[a-zA-Z0-9-._~+/]+=*)$",
            Pattern.CASE_INSENSITIVE);

    private AuthenticationManager authenticationManager;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Authentication authenticationRequest = null;
        try {
            // 将token字符串转换为Authentication对象
            authenticationRequest = this.convert(request);
        } catch (AuthenticationException e) {
            SecurityContextHolder.clearContext();
            throw e;
        }
        if (authenticationRequest == null) {
            filterChain.doFilter(request, response);
            return;
        }
        // 委托处理认证
        try {
            Authentication authentication = this.authenticationManager.authenticate(authenticationRequest);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (AuthenticationException e) {
            SecurityContextHolder.clearContext();
            throw e;
        }
    }

    private Authentication convert(HttpServletRequest request) {
        String token = resolveFromAuthorizationHeader(request);
        if (StringUtils.hasText(token)) {
            return new XcmsBearerTokenAuthenticationToken(token);
        }
        return null;
    }

    private String resolveFromAuthorizationHeader(HttpServletRequest request) {
        String authorization = request.getHeader(this.bearerTokenHeaderName);
        if (!StringUtils.startsWithIgnoreCase(authorization, "bearer")) {
            return null;
        }
        Matcher matcher = authorizationPattern.matcher(authorization);
        if (!matcher.matches()) {
            throw new InvalidTokenAuthenticationException("Bearer token 格式不正确.");
        }
        return matcher.group("token");
    }
}
