package com.df4j.xctec.xcms.base.jpa.id;

import com.df4j.xctec.xcms.core.id.IdGenerateStrategy;

/**
 * 可插拔 ID 生成器路由：持有当前策略实例，供 XcmsIdGenerator（Hibernate Generator）调用。
 *
 * 由 IdGeneratorConfig 在 BeanFactoryPostProcessor 阶段注入，
 * 早于 EntityManagerFactory 完成初始化。
 */
public final class IdGeneratorHolder {

    private static volatile IdGenerateStrategy strategy;

    private IdGeneratorHolder() {
    }

    public static void set(IdGenerateStrategy strategy) {
        IdGeneratorHolder.strategy = strategy;
    }

    public static IdGenerateStrategy get() {
        if (strategy == null) {
            throw new IllegalStateException("IdGenerateStrategy not initialized, " +
                    "ensure IdGeneratorConfig is loaded before any entity persist");
        }
        return strategy;
    }
}
