package com.df4j.xctec.xcms.common.security.authentication.provider;

import com.df4j.xctec.xcms.common.security.authentication.token.XcmsBearerTokenAuthenticationToken;
import com.df4j.xctec.xcms.common.security.exception.InvalidTokenAuthenticationException;
import com.df4j.xctec.xcms.common.security.token.TokenManager;
import org.jspecify.annotations.Nullable;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

public class XcmsBearerTokenAuthenticationProvider implements AuthenticationProvider {

    private final TokenManager tokenManager;

    public XcmsBearerTokenAuthenticationProvider(TokenManager tokenManager) {
        this.tokenManager = tokenManager;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public @Nullable Authentication authenticate(Authentication authentication) throws AuthenticationException {

        XcmsBearerTokenAuthenticationToken authenticationToken = (XcmsBearerTokenAuthenticationToken) authentication;

        if (!tokenManager.validate(authenticationToken.getToken())) {
            throw new InvalidTokenAuthenticationException("token无效！");
        }
        return tokenManager.parse(authenticationToken.getToken());
    }

    @Override
    public boolean supports(@SuppressWarnings("NullableProblems") Class<?> authentication) {
        return XcmsBearerTokenAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
