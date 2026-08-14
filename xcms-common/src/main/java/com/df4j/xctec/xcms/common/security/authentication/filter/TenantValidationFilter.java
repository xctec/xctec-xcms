package com.df4j.xctec.xcms.common.security.authentication.filter;

import com.df4j.xctec.xcms.common.security.authentication.AuthUser;
import com.df4j.xctec.xcms.common.security.authentication.handler.XcmsAccessDeniedHandler;
import com.df4j.xctec.xcms.common.security.authentication.userdetails.XcmsUserDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 租户防伪造校验过滤器。
 * <p>
 * 位于 {@link XcmsBearerTokenAuthenticationFilter} 之后：在 Bearer 认证建立 principal 之后，
 * 校验请求头 {@code X-Tenant-Id} 是否等于登录主体（principal）所属租户。
 * 不一致（伪造租户切换）一律拒绝，返回 403（{@code errorNo=-403}）。
 * <p>
 * 拒绝时直接通过 {@link XcmsAccessDeniedHandler} 写出 403 响应并终止过滤器链，
 * 不依赖 {@code ExceptionTranslationFilter} 的捕获顺序（本过滤器位于其上游，抛异常会冒泡成 500）。
 * 未认证路径（无合法 Bearer / permitAll 路径）直接放行，
 * 由 {@code authorizeHttpRequests} 的 {@code permitAll/authenticated} 规则决定最终是否放行。
 */
public class TenantValidationFilter extends OncePerRequestFilter {

    private static final String TENANT_HEADER = "X-Tenant-Id";

    private final XcmsAccessDeniedHandler accessDeniedHandler = new XcmsAccessDeniedHandler();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // 未认证（无合法 Bearer / permitAll 路径）跳过校验
        if (authentication == null || !authentication.isAuthenticated()) {
            filterChain.doFilter(request, response);
            return;
        }

        Long principalTenantId = extractTenantId(authentication);
        // 主体无租户信息（理论上不应发生）放行，避免误伤
        if (principalTenantId == null) {
            filterChain.doFilter(request, response);
            return;
        }

        String headerTenant = request.getHeader(TENANT_HEADER);
        if (headerTenant == null || headerTenant.isBlank()) {
            deny(request, response, "缺少租户标识（X-Tenant-Id），拒绝访问");
            return;
        }
        Long headerTenantId;
        try {
            headerTenantId = Long.valueOf(headerTenant.trim());
        } catch (NumberFormatException e) {
            deny(request, response, "租户标识非法（X-Tenant-Id），拒绝访问");
            return;
        }

        if (!principalTenantId.equals(headerTenantId)) {
            deny(request, response, "租户标识与登录租户不一致，拒绝访问");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void deny(HttpServletRequest request, HttpServletResponse response, String message) throws IOException, ServletException {
        accessDeniedHandler.handle(request, response, new AccessDeniedException(message));
    }

    private Long extractTenantId(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (principal instanceof XcmsUserDetails userDetails) {
            return userDetails.getTenantId();
        }
        if (principal instanceof AuthUser authUser) {
            return authUser.getTenantId();
        }
        return null;
    }
}
