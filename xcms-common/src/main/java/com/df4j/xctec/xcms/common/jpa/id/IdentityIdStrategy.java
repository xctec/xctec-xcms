package com.df4j.xctec.xcms.common.jpa.id;

/**
 * 数据库自增策略。实际 ID 由数据库在 INSERT 时生成，
 * 该策略仅用于占位声明；调用 nextId 直接抛异常（自增不走应用层生成）。
 */
public class IdentityIdStrategy implements IdGenerateStrategy {

    @Override
    public long nextId(Class<?> entityClass) {
        throw new UnsupportedOperationException(
                "identity strategy does not generate id in application layer");
    }

    @Override
    public String name() {
        return "identity";
    }
}
