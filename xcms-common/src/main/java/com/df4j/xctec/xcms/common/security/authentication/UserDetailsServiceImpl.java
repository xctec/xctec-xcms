package com.df4j.xctec.xcms.common.security.authentication;

import com.df4j.xctec.xcms.common.security.authentication.userdetails.XcmsUserDetails;
import com.df4j.xctec.xcms.core.context.tenant.TenantContextUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Spring Security 的 {@link UserDetailsService} 实现，作为认证基础设施的一部分置于 common。
 * <p>
 * 仅依赖 common 层定义的 {@link AuthUserProvider} 端口取数，不直接引用任何业务模块的实现层
 * （实体 / Repository），从而解耦认证与具体业务模块。数据提供方的实现（如 JPA 适配器）
 * 由持有用户数据的模块以 Spring Bean 形式提供。
 * </p>
 * <p>
 * 通过 {@link ConditionalOnBean} 守卫：仅当容器中存在 {@link AuthUserProvider} 实现时才注册本 bean，
 * 以保全 {@code SecurityConfig} 中 {@code UserDetailsService} 的可选语义——未提供平台用户数据源的模块
 * 仍可仅使用 Bearer Token 认证，不会因本 bean 的构造依赖缺失而启动失败。
 * </p>
 * <p>
 * 租户标识从 {@code TenantContext}（请求头 X-Tenant-Id 经 TenantContextFilter 写入）直接获取，
 * principal 仅传入 username。本服务据此结合上下文中的 tenantId 定位用户。
 * {@code XcmsUserDetails.tenantId} 用于登录成功后回写 {@code TenantContext}。
 * 角色/权限目前由 {@code XcmsUserDetails.roleCodes} 承载（数据接入角色表后自动生效）。
 * </p>
 */
@Service
@ConditionalOnBean(AuthUserProvider.class)
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final AuthUserProvider authUserProvider;

    /**
     * 租户从上下文直接取得，principal 仅需 username。
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Long tenantId = TenantContextUtils.requireTenantId();

        AuthUser authUser = authUserProvider.loadByTenantAndUsername(tenantId, username)
                .orElseThrow(() -> new UsernameNotFoundException("用户不存在"));

        XcmsUserDetails details = new XcmsUserDetails();
        details.setUserId(authUser.getUserId());
        details.setUsername(authUser.getUsername());
        details.setPassword(authUser.getPassword());
        details.setNickname(authUser.getNickname());
        details.setTenantId(authUser.getTenantId());
        details.setAvatar(authUser.getAvatar());
        details.setMobile(authUser.getMobile());
        details.setEmail(authUser.getEmail());
        details.setEnableStatus(authUser.getEnableStatus());
        details.setRoleNames(authUser.getRoleNames());
        details.setOrgUnitName(authUser.getOrgUnitName());
        return details;
    }
}
