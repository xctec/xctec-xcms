package com.df4j.xctec.xcms.common.security.userdetails;

import lombok.Data;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Data
public class XcmsUserDetails implements UserDetails {

    private Long userId;
    private String username;
    private String nickname;
    private Long tenantId;
    private String avatar;
    private String mobile;
    private String email;
    private String password;
    private Set<String> roleCodes;
    private String enableStatus;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public @Nullable String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }
}
