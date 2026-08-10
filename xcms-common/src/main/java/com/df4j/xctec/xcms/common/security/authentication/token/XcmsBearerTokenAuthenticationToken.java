package com.df4j.xctec.xcms.common.security.authentication.token;

import lombok.Getter;
import org.jspecify.annotations.Nullable;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.Collections;


@Getter
public class XcmsBearerTokenAuthenticationToken extends AbstractAuthenticationToken {

    private final String token;

    public XcmsBearerTokenAuthenticationToken(String token) {
        super(Collections.emptyList());
        Assert.hasText(token, "Bearer token cannot be empty.");
        this.token = token;
    }

    public XcmsBearerTokenAuthenticationToken(String token, @Nullable Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        Assert.hasText(token, "Bearer token cannot be empty.");
        this.token = token;
        super.setAuthenticated(true);
    }

    @Override
    public @Nullable Object getCredentials() {
        return this.getToken();
    }

    @Override
    public @Nullable Object getPrincipal() {
        return this.getToken();
    }

    @Override
    public void setAuthenticated(boolean authenticated) {
        Assert.isTrue(!authenticated,
                "Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead");
        super.setAuthenticated(false);
    }

    public static XcmsBearerTokenAuthenticationToken authenticated(String token, Collection<? extends GrantedAuthority> authorities) {
        return new XcmsBearerTokenAuthenticationToken(token, authorities);
    }
}
