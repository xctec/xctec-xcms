package com.df4j.xctec.xcms.core.context.token;

import java.util.Optional;

public class TokenContextUtils {
    private static TokenContextAccessor instance;

    public static void bind(TokenContextAccessor tokenContextAccessor) {
        instance = tokenContextAccessor;
    }

    private static TokenContextAccessor current() {
        TokenContextAccessor accessor = instance;
        if (accessor == null) {
            throw new IllegalStateException("TokenContextAccessor 尚未初始化，请确认已经注入");
        }
        return accessor;
    }

    public static void setToken(String token) {
        current().set(token);
    }

    public static void clear() {
        current().clear();
    }

    public static String requireToken() {
        return current().get();
    }

    public static Optional<String> token() {
        return Optional.ofNullable(current().getOrNull());
    }
}
