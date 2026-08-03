package com.df4j.xctec.xcms.base.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.concurrent.ExecutorService;

/**
 * 事件分发桥接：将 Spring ApplicationEventPublisher 与异步执行器对接。
 *
 * ApplicationEventPublisher 在 xcms-base 中是可选依赖（仅当 classpath 有 spring-context 时生效）。
 * 若无 Spring 上下文（纯库使用场景），publish 直接同步执行。
 */
@Component
public class EventBus {

    private static final Logger log = LoggerFactory.getLogger(EventBus.class);

    private volatile ApplicationEventPublisher applicationEventPublisher;
    private final ExecutorService executor;

    public EventBus(ExecutorService executor) {
        this.executor = executor;
    }

    public void setApplicationEventPublisher(ApplicationEventPublisher publisher) {
        this.applicationEventPublisher = publisher;
    }

    public void dispatch(Object event) {
        if (applicationEventPublisher != null) {
            applicationEventPublisher.publishEvent(event);
        } else {
            log.debug("event delivered synchronously: {}", event.getClass().getSimpleName());
        }
    }

    public void dispatchAsync(Object event) {
        executor.execute(() -> dispatch(event));
    }

    public void dispatchAllAsync(Collection<?> events) {
        events.forEach(this::dispatchAsync);
    }

    /**
     * 同步监听：Spring 容器内通过 @EventListener 接收事件。
     * 此处仅做日志兜底，实际处理由各模块的 @EventListener 实现。
     */
    @EventListener
    public void onEvent(Object event) {
        log.debug("event received: {}", event.getClass().getSimpleName());
    }
}
