package com.df4j.xctec.xcms.common.security.exception;


import org.jspecify.annotations.Nullable;
import org.springframework.security.core.AuthenticationException;

public class InvalidTokenAuthenticationException extends AuthenticationException {

    public InvalidTokenAuthenticationException(@Nullable String msg) {
        super(msg);
    }

    public InvalidTokenAuthenticationException(@Nullable String msg, Throwable cause) {
        super(msg, cause);
    }
}
