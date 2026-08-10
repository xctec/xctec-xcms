package com.df4j.xctec.xcms.common.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * 安全相关配置，从 application.yml 读取（前缀 xcms.security）。
 * <p>公开路径通过配置下发，避免在配置类中硬编码。</p>
 */
@ConfigurationProperties(prefix = "xcms.security")
public class SecurityProperties {

    /**
     * 公开端点（无需 Bearer Token 即可访问，但仍会经过安全过滤器链与上下文过滤器）。
     * 其余请求一律需要有效认证。默认包含认证接口、错误页。
     */
    private List<String> permitAllMatchers = List.of(
            "/auth/**",
            "/error"
    );

    /**
     * 完全忽略的端点：不走任何 Spring Security 过滤器链（不解析 Token、不写入上下文）。
     * 适用于健康检查、监控、静态资源等无需任何安全处理的请求。
     */
    private List<String> ignoreMatchers = List.of(
            "/actuator/**",
            "/actuator",
            "/h2-console/**"
    );

    public List<String> getPermitAllMatchers() {
        return permitAllMatchers;
    }

    public void setPermitAllMatchers(List<String> permitAllMatchers) {
        this.permitAllMatchers = permitAllMatchers;
    }

    public List<String> getIgnoreMatchers() {
        return ignoreMatchers;
    }

    public void setIgnoreMatchers(List<String> ignoreMatchers) {
        this.ignoreMatchers = ignoreMatchers;
    }
}
