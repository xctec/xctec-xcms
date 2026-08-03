package com.df4j.xctec.xcms.web.filter;

import com.df4j.xctec.xcms.core.security.AuthPrincipal;
import com.df4j.xctec.xcms.core.tenant.TenantContext;
import com.df4j.xctec.xcms.core.tenant.TenantInfo;
import com.df4j.xctec.xcms.web.security.SecurityContextHolder;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 租户上下文过滤器：从 SecurityContextHolder 读取 AuthPrincipal，
 * 构建 TenantInfo 并放入 TenantContext。
 *
 * 执行顺序：TraceIdFilter → TokenAuthenticationFilter → TenantContextFilter。
 * TenantContextFilter 在 SecurityFilterChain 之前执行，确保 Controller 与 JPA 能读到租户上下文。
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 20)
public class TenantContextFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        AuthPrincipal principal = SecurityContextHolder.get().orElse(null);
        if (principal != null && principal.tenantId() != null) {
            TenantInfo info = new TenantInfo(
                    principal.tenantId(),
                    null,
                    null,
                    principal.tenantPath(),
                    principal.tenantCodePath(),
                    null);
            TenantContext.runWith(info, () -> {
                try {
                    filterChain.doFilter(request, response);
                } catch (IOException | ServletException e) {
                    throw new RuntimeException(e);
                }
            });
        } else {
            filterChain.doFilter(request, response);
        }
    }
}
