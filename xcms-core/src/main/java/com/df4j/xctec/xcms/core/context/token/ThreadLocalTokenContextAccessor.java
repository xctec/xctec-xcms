package com.df4j.xctec.xcms.core.context.token;

public class ThreadLocalTokenContextAccessor implements TokenContextAccessor {
    private final ThreadLocal<String> threadLocal = new ThreadLocal<>();

    @Override
    public void set(String token) {
        threadLocal.set(token);
    }

    @Override
    public void clear() {
        threadLocal.remove();
    }

    @Override
    public String getOrNull() {
        return threadLocal.get();
    }
}
