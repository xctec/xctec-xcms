package com.df4j.xctec.xcms.auth.service;

import com.df4j.xctec.xcms.common.security.authentication.userdetails.XcmsUserDetails;
import com.df4j.xctec.xcms.core.context.tenant.TenantContextUtils;
import com.df4j.xctec.xcms.system.domain.entity.User;
import com.df4j.xctec.xcms.system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Spring Security 的 {@link UserDetailsService} 实现。
 * <p>
 * 租户标识从 {@code TenantContext}（请求头 X-Tenant-Id 经 TenantContextFilter 写入）直接获取，
 * principal 仅传入 username。本服务据此结合上下文中的 tenantId 定位用户。
 * {@code XcmsUserDetails.tenantId} 用于登录成功后回写 {@code TenantContext}。
 * 角色/权限目前由 {@code XcmsUserDetails.roleCodes} 承载（数据接入角色表后自动生效）。
 * </p>
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * 租户从上下文直接取得，principal 仅需 username。
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Long tenantId = TenantContextUtils.requireTenantId();

        User user = userRepository.findByTenantIdAndUsername(tenantId, username)
                .orElseThrow(() -> new UsernameNotFoundException("用户不存在"));

        XcmsUserDetails details = new XcmsUserDetails();
        details.setUserId(user.getId());
        details.setUsername(user.getUsername());
        details.setPassword(user.getPassword());
        details.setNickname(user.getNickname());
        details.setTenantId(user.getTenantId());
        details.setAvatar(user.getAvatar());
        details.setMobile(user.getMobile());
        details.setEmail(user.getEmail());
        details.setEnableStatus(user.getEnableStatus());
        return details;
    }
}
