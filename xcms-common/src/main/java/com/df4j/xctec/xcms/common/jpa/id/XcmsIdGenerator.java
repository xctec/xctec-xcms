package com.df4j.xctec.xcms.common.jpa.id;

import org.springframework.stereotype.Component;

/**
 * 应用层统一 ID 生成入口，委托给 IdGeneratorHolder。
 */
@Component
public class XcmsIdGenerator {

    private final IdGeneratorHolder holder;

    public XcmsIdGenerator(IdGeneratorHolder holder) {
        this.holder = holder;
    }

    public long nextId(Class<?> entityClass) {
        return holder.nextId(entityClass);
    }

    public long nextId(String strategyName, Class<?> entityClass) {
        return holder.nextId(strategyName, entityClass);
    }
}
