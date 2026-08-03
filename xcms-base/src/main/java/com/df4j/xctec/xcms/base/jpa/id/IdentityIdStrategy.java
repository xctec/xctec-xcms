package com.df4j.xctec.xcms.base.jpa.id;

import com.df4j.xctec.xcms.core.id.IdGenerateStrategy;

/**
 * 数据库自增策略。
 * 实际 ID 由数据库在 INSERT 时生成，该策略仅用于占位声明；
 * 调用 nextId() 直接抛异常（自增不走应用层生成）。
 */
public class IdentityIdStrategy implements IdGenerateStrategy {

    @Override
    public boolean databaseGenerated() {
        return true;
    }

    @Override
    public Long nextId() {
        throw new UnsupportedOperationException("identity strategy does not generate id in application layer");
    }

    @Override
    public String name() {
        return "identity";
    }
}
