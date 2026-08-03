package com.df4j.xctec.xcms.common.jpa.id;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 可插拔 ID 生成器路由：按 name 注册多个策略，缺省用默认策略。
 * 树形实体的 path 不含自身 id，故可一次性写入，无需回填。
 */
public class IdGeneratorHolder {

    private final Map<String, IdGenerateStrategy> strategies = new ConcurrentHashMap<>();
    private IdGenerateStrategy defaultStrategy;

    public void register(IdGenerateStrategy strategy) {
        strategies.put(strategy.name(), strategy);
        if (defaultStrategy == null) {
            defaultStrategy = strategy;
        }
    }

    public void setDefault(IdGenerateStrategy strategy) {
        register(strategy);
        defaultStrategy = strategy;
    }

    public IdGenerateStrategy get(String name) {
        IdGenerateStrategy s = strategies.get(name);
        if (s == null) {
            throw new IllegalArgumentException("unknown id strategy: " + name);
        }
        return s;
    }

    public IdGenerateStrategy getDefault() {
        return defaultStrategy;
    }

    public long nextId(Class<?> entityClass) {
        return defaultStrategy.nextId(entityClass);
    }

    public long nextId(String name, Class<?> entityClass) {
        return get(name).nextId(entityClass);
    }
}
