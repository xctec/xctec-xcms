package com.df4j.xctec.xcms.common.security.authentication.filter;

import com.df4j.xctec.xcms.common.security.exception.InvalidTokenAuthenticationException;
import com.df4j.xctec.xcms.core.context.token.TokenContextUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TokenContextFilter extends OncePerRequestFilter {

    @SuppressWarnings("FieldCanBeLocal")
    private final String bearerTokenHeaderName = HttpHeaders.AUTHORIZATION;

    private static final Pattern authorizationPattern = Pattern.compile("^Bearer (?<token>[a-zA-Z0-9-._~+/]+=*)$",
            Pattern.CASE_INSENSITIVE);


    @SuppressWarnings("NullableProblems")
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = resolveFromAuthorizationHeader(request);
        if (StringUtils.hasText(token)) {
            try {
                TokenContextUtils.setToken(token);
                filterChain.doFilter(request, response);
            } finally {
                TokenContextUtils.clear();
            }
        } else {
            filterChain.doFilter(request, response);
        }
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
