package com.df4j.xctec.xcms.common.security.authentication.filter;

import com.df4j.xctec.xcms.core.context.tenant.TenantContextUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class TenantContextFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("X-Tenant-Id");
        Long tenantId = null;
        if (StringUtils.hasText(header)) {
            try {
                tenantId = Long.parseLong(header.trim());
            } catch (NumberFormatException e) {
                tenantId = null;
            }
        }
        if (tenantId == null) {
            // 没有合法的租户头，不写入上下文，交由业务/认证过滤器决定是否拦截
            filterChain.doFilter(request, response);
        } else {
            try {
                TenantContextUtils.setTenantId(tenantId);
                filterChain.doFilter(request, response);
            } finally {
                TenantContextUtils.clear();
            }
        }
    }
}
