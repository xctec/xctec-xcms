package com.df4j.xctec.xcms.common.configuration;

import com.df4j.xctec.xcms.common.configuration.properties.SecurityProperties;
import com.df4j.xctec.xcms.common.security.authentication.filter.TenantContextFilter;
import com.df4j.xctec.xcms.common.security.authentication.filter.TenantValidationFilter;
import com.df4j.xctec.xcms.common.security.authentication.filter.TokenContextFilter;
import com.df4j.xctec.xcms.common.security.authentication.filter.XcmsBearerTokenAuthenticationFilter;
import com.df4j.xctec.xcms.common.security.authentication.handler.XcmsAccessDeniedHandler;
import com.df4j.xctec.xcms.common.security.authentication.handler.XcmsAuthenticationEntryPoint;
import com.df4j.xctec.xcms.common.security.authentication.provider.XcmsBearerTokenAuthenticationProvider;
import com.df4j.xctec.xcms.common.security.token.TokenManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.ArrayList;
import java.util.List;

/**
 * 通用安全装配：Bearer Token 认证 + 上下文过滤器 + 无状态会话。
 * <p>
 * 该配置放在 common 中，任何引入 common 并依赖 spring-boot-starter-security 的模块都会自动获得
 * 一致的认证能力，便于后续按模块拆分为微服务。公开路径由 {@link SecurityProperties} 从配置文件读取。
 */
@Configuration
@EnableWebSecurity
@EnableConfigurationProperties(SecurityProperties.class)
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 可选注入：若模块提供了 {@link UserDetailsService}（如 xcms-auth-impl 的 UserDetailsServiceImpl），
     * 则启用基于用户名密码的本地认证（DaoAuthenticationProvider），否则仅保留 Bearer 认证。
     */
    @Autowired(required = false)
    private UserDetailsService userDetailsService;

    @Bean
    public AuthenticationManager authenticationManager(TokenManager tokenManager, PasswordEncoder passwordEncoder) {
        List<org.springframework.security.authentication.AuthenticationProvider> providers = new ArrayList<>();
        providers.add(new XcmsBearerTokenAuthenticationProvider(tokenManager));
        if (userDetailsService != null) {
            DaoAuthenticationProvider daoProvider = new DaoAuthenticationProvider(userDetailsService);
            daoProvider.setPasswordEncoder(passwordEncoder);
            providers.add(daoProvider);
        }
        return new ProviderManager(providers);
    }

    /**
     * 完全忽略的端点：跳过整个 Spring Security 过滤器链（不解析 Token、不写入上下文）。
     * 与 {@code permitAllMatchers}（免认证但走过滤器链）区分，用于健康检查、监控、静态资源等。
     * <p>
     * 官方推荐方式：通过 {@link WebSecurityCustomizer} 配置 {@code web.ignoring()}，
     * 这些请求不会进入任何 SecurityFilterChain。
     */
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer(SecurityProperties securityProperties) {
        return web -> web.ignoring()
                .requestMatchers(securityProperties.getIgnoreMatchers().toArray(new String[0]));
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   AuthenticationManager authenticationManager,
                                                   SecurityProperties securityProperties) throws Exception {
        XcmsBearerTokenAuthenticationFilter bearerFilter = new XcmsBearerTokenAuthenticationFilter();
        bearerFilter.setAuthenticationManager(authenticationManager);

        List<String> publicMatchers = securityProperties.getPermitAllMatchers();

        http
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .anonymous(AbstractHttpConfigurer::disable)
                .rememberMe(AbstractHttpConfigurer::disable)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers(publicMatchers.toArray(new String[0])).permitAll();
                    auth.anyRequest().authenticated();
                })
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(new XcmsAuthenticationEntryPoint())
                        .accessDeniedHandler(new XcmsAccessDeniedHandler())
                )
                .headers(h -> h.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                .addFilterBefore(bearerFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new TenantContextFilter(), XcmsBearerTokenAuthenticationFilter.class)
                .addFilterBefore(new TokenContextFilter(), XcmsBearerTokenAuthenticationFilter.class)
                .addFilterAfter(new TenantValidationFilter(), XcmsBearerTokenAuthenticationFilter.class);
        return http.build();
    }
}
