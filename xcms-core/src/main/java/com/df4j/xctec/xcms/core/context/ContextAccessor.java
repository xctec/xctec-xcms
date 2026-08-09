package com.df4j.xctec.xcms.core.context;

import com.df4j.xctec.xcms.core.exception.ContextException;

public interface ContextAccessor<T> {
    void set(T context);

    void clear();

    T getOrNull();

    default T get() {
        T context = getOrNull();
        if (context == null) {
            throw ContextException.noContext();
        }
        return context;
    }
}
