package com.df4j.xctec.xcms.core.context;

import lombok.Getter;
import org.springframework.beans.factory.SmartInitializingSingleton;

public abstract class ContextBinder<T extends ContextAccessor<?>> implements SmartInitializingSingleton {

    @Getter
    private T contextAccess;

    public ContextBinder(T contextAccess) {
        this.contextAccess = contextAccess;
    }
}
