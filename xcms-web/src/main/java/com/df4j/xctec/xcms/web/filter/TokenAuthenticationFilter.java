package com.df4j.xctec.xcms.web.filter;

import com.df4j.xctec.xcms.core.security.AuthPrincipal;
import com.df4j.xctec.xcms.core.security.TokenStore;
import com.df4j.xctec.xcms.web.security.SecurityContextHolder;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Token 认证过滤器：解析 accessToken，填充 SecurityContextHolder。
 *
 * 执行顺序：TraceIdFilter → TokenAuthenticationFilter → TenantContextFilter。
 *
 * 注意：此过滤器不拦截认证白名单路径（登录、刷新令牌等）。
 * 白名单由 SecurityFilterChain 配置，此处只负责"有 token 则解析"。
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    public static final String AUTH_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";

    private final TokenStore tokenStore;

    public TokenAuthenticationFilter(TokenStore tokenStore) {
        this.tokenStore = tokenStore;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = extractToken(request);
        if (StringUtils.hasText(token)) {
            tokenStore.verify(token).ifPresent(SecurityContextHolder::set);
        }
        try {
            filterChain.doFilter(request, response);
        } finally {
            SecurityContextHolder.clear();
        }
    }

    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader(AUTH_HEADER);
        if (StringUtils.hasText(header) && header.startsWith(BEARER_PREFIX)) {
            return header.substring(BEARER_PREFIX.length()).trim();
        }
        return null;
    }
}
