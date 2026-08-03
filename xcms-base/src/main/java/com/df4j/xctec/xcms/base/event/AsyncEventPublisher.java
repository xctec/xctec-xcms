package com.df4j.xctec.xcms.base.event;

import com.df4j.xctec.xcms.core.event.EventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.Collection;

/**
 * 事件发布器默认实现。
 *
 * - publish：立即异步投递。
 * - publishAfterCommit：注册 TransactionSynchronization，事务提交后投递，
 *   避免「事件已发但事务回滚」。审计走这条。
 *   无事务时降级为立即异步投递并记 warn 日志。
 */
@Component
@ConditionalOnMissingBean(EventPublisher.class)
public class AsyncEventPublisher implements EventPublisher {

    private static final Logger log = LoggerFactory.getLogger(AsyncEventPublisher.class);

    private final EventBus eventBus;

    public AsyncEventPublisher(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public void publish(Object event) {
        eventBus.dispatchAsync(event);
    }

    @Override
    public void publishAfterCommit(Object event) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    eventBus.dispatchAsync(event);
                }
            });
        } else {
            log.warn("publishAfterCommit called without active transaction, falling back to immediate async dispatch: {}",
                    event.getClass().getSimpleName());
            eventBus.dispatchAsync(event);
        }
    }

    @Override
    public void publishAll(Collection<?> events) {
        if (events == null || events.isEmpty()) {
            return;
        }
        events.forEach(this::publish);
    }
}
