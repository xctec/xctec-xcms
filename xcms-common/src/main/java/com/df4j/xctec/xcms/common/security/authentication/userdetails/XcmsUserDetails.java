package com.df4j.xctec.xcms.common.security.authentication.userdetails;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;


@Data
public class XcmsUserDetails implements UserDetails {

    public static final String ENABLED = "ENABLED";

    private Long userId;
    private String username;
    private String nickname;
    private Long tenantId;
    private String avatar;
    private String mobile;
    private String email;
    private String password;
    private String encType;
    private Set<String> roleCodes;
    private String enableStatus;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (roleCodes == null || roleCodes.isEmpty()) {
            return List.of();
        }
        return roleCodes.stream()
                .filter(Objects::nonNull)
                .map(code -> new SimpleGrantedAuthority("ROLE_" + code))
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return ENABLED.equalsIgnoreCase(this.enableStatus);
    }
}
