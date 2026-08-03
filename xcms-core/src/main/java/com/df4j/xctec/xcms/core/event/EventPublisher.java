package com.df4j.xctec.xcms.core.event;

import java.util.Collection;

/**
 * 事件发布抽象。
 * 接口定义于 xcms-core，AsyncEventPublisher 实现在 xcms-base。
 *
 * - publish：立即异步投递。
 * - publishAfterCommit：注册 TransactionSynchronization，事务提交后投递，
 *   避免「事件已发但事务回滚」。审计走这条。
 */
public interface EventPublisher {

    void publish(Object event);

    void publishAfterCommit(Object event);

    void publishAll(Collection<?> events);
}
