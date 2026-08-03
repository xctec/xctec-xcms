package com.df4j.xctec.xcms.common.security;

import java.io.Serializable;
import java.time.Instant;

/**
 * 令牌对：accessToken（短期）+ refreshToken（长期）。
 */
public class TokenPair implements Serializable {

    private String accessToken;
    private String refreshToken;
    private Instant accessExpiresAt;
    private Instant refreshExpiresAt;

    public TokenPair() {
    }

    public TokenPair(String accessToken, String refreshToken,
                     Instant accessExpiresAt, Instant refreshExpiresAt) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.accessExpiresAt = accessExpiresAt;
        this.refreshExpiresAt = refreshExpiresAt;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public Instant getAccessExpiresAt() {
        return accessExpiresAt;
    }

    public void setAccessExpiresAt(Instant accessExpiresAt) {
        this.accessExpiresAt = accessExpiresAt;
    }

    public Instant getRefreshExpiresAt() {
        return refreshExpiresAt;
    }

    public void setRefreshExpiresAt(Instant refreshExpiresAt) {
        this.refreshExpiresAt = refreshExpiresAt;
    }
}
