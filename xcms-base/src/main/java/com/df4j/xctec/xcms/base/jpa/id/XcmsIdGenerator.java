package com.df4j.xctec.xcms.base.jpa.id;

import com.df4j.xctec.xcms.base.jpa.BaseEntity;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.generator.BeforeExecutionGenerator;
import org.hibernate.generator.EventType;
import org.hibernate.generator.EventTypeSets;
import org.hibernate.generator.OnExecutionGenerator;

import java.util.EnumSet;

/**
 * Hibernate ID 生成器，桥接到 IdGeneratorHolder 持有的策略。
 *
 * 同时实现 BeforeExecutionGenerator（雪花/号段，应用层生成）与 OnExecutionGenerator（identity，数据库生成），
 * 运行期按 generatedOnExecution() 分流：
 * - snowflake / segment：generatedOnExecution() = false，走 generate() 在 persist 前生成 ID
 * - identity：generatedOnExecution() = true，由数据库在 INSERT 时生成并回写
 *
 * 允许业务预先赋值（数据迁移场景）：若 BaseEntity.id != null 则直接使用。
 */
public class XcmsIdGenerator implements BeforeExecutionGenerator, OnExecutionGenerator {

    @Override
    public boolean generatedOnExecution() {
        // true 走数据库自增，false 走 Java 侧生成
        return IdGeneratorHolder.get().databaseGenerated();
    }

    @Override
    public EnumSet<EventType> getEventTypes() {
        return EventTypeSets.INSERT_ONLY;
    }

    // ---- BeforeExecution 分支：雪花 / 号段 ----

    @Override
    public Object generate(SharedSessionContractImplementor session, Object owner,
                           Object currentValue, EventType eventType) {
        // 允许业务预先赋值（数据迁移场景）
        if (owner instanceof BaseEntity e && e.getId() != null) {
            return e.getId();
        }
        return IdGeneratorHolder.get().nextId();
    }

    // ---- OnExecution 分支：数据库自增 ----

    @Override
    public boolean referenceColumnsInSql(Dialect dialect) {
        return false; // INSERT 语句不包含 id 列，由数据库自动生成
    }

    @Override
    public String[] getReferencedColumnValues(Dialect dialect) {
        return null;
    }

    @Override
    public boolean writePropertyValue() {
        return false; // 值由数据库回写到实体
    }
}
