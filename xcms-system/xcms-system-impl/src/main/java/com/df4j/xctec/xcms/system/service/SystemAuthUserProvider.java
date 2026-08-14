package com.df4j.xctec.xcms.system.service;

import com.df4j.xctec.xcms.common.security.authentication.AuthUser;
import com.df4j.xctec.xcms.common.security.authentication.AuthUserProvider;
import com.df4j.xctec.xcms.system.domain.entity.Role;
import com.df4j.xctec.xcms.system.domain.entity.User;
import com.df4j.xctec.xcms.system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * {@link AuthUserProvider} 的 JPA 实现，由 system 模块提供，承载用户数据访问细节。
 * 认证模块通过此端口取数，无需直接依赖 system 实体或 Repository。
 */
@Service
@RequiredArgsConstructor
public class SystemAuthUserProvider implements AuthUserProvider {

    private final UserRepository userRepository;

    @Override
    public Optional<AuthUser> loadByTenantAndUsername(Long tenantId, String username) {
        return userRepository.findByTenantIdAndUsername(tenantId, username)
                .map(this::toAuthUser);
    }

    @SuppressWarnings("null")
    private AuthUser toAuthUser(User user) {
        AuthUser authUser = new AuthUser();
        authUser.setUserId(user.getId());
        authUser.setUsername(user.getUsername());
        authUser.setPassword(user.getPassword());
        authUser.setNickname(user.getNickname());
        authUser.setTenantId(user.getTenantId());
        authUser.setAvatar(user.getAvatar());
        authUser.setMobile(user.getMobile());
        authUser.setEmail(user.getEmail());
        authUser.setEnableStatus(user.getEnableStatus());
        authUser.setRoleNames(user.getRoles().stream()
                .map(Role::getRoleName)
                .filter(Objects::nonNull)
                .collect(Collectors.toList()));
        authUser.setRoleCodes(user.getRoles().stream()
                .map(Role::getRoleCode)
                .filter(Objects::nonNull)
                .collect(Collectors.toList()));
        authUser.setOrgUnitName(user.getOrgUnit() == null ? null : user.getOrgUnit().getOrgName());
        return authUser;
    }
}
