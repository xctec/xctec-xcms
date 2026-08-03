package com.df4j.xctec.xcms.common.event;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Component;

/**
 * 把 AbstractEvent 适配为 Spring ApplicationEvent 并广播，
 * 由 @EventListener / @TransactionalEventListener 监听器消费。
 */
@Component
public class EventBus implements ApplicationEventPublisherAware {

    private static ApplicationEventPublisher publisher;

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        publisher = applicationEventPublisher;
    }

    public static void dispatch(AbstractEvent event) {
        if (publisher != null) {
            publisher.publishEvent(event);
        }
    }
}
