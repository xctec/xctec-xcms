package com.df4j.xctec.xcms.core.context;

public class ThreadLocalContextAccessor<T> implements ContextAccessor<T> {

    private ThreadLocal<T> threadLocal = new ThreadLocal<>();

    @Override
    public void set(T context) {
        threadLocal.set(context);
    }

    @Override
    public void clear() {
        threadLocal.remove();
    }

    @Override
    public T getOrNull() {
        return threadLocal.get();
    }
}
