package com.df4j.xctec.xcms.core.security;

import java.io.Serializable;

/**
 * 令牌对：accessToken（短期）+ refreshToken（长期）。
 */
public record TokenPair(
        String accessToken,
        String refreshToken,
        Long expiresIn,
        Long refreshExpiresIn
) implements Serializable {
}
