package com.df4j.xctec.xcms.common.event;

import com.alibaba.ttl.threadpool.TtlExecutors;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 事件异步执行器配置：TTL 包装，保证线程上下文透传。
 */
@Configuration
public class EventExecutorConfig {

    @Bean
    public Executor eventExecutor() {
        ExecutorService delegate = new ThreadPoolExecutor(
                4, 16, 60L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(1024),
                r -> {
                    Thread t = new Thread(r, "xcms-event-");
                    t.setDaemon(true);
                    return t;
                },
                new ThreadPoolExecutor.CallerRunsPolicy());
        return TtlExecutors.getTtlExecutor(delegate);
    }

    @Bean
    public EventPublisher eventPublisher(Executor eventExecutor) {
        return new AsyncEventPublisher(eventExecutor);
    }
}
