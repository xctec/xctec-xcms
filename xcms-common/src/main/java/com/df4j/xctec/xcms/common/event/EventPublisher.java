package com.df4j.xctec.xcms.common.event;

/**
 * 事件发布抽象。
 * publish：即时发布（可能在事务内）。
 * publishAfterCommit：事务提交后发布（审计、跨服务一致性常用）。
 */
public interface EventPublisher {

    void publish(AbstractEvent event);

    void publishAfterCommit(AbstractEvent event);
}
