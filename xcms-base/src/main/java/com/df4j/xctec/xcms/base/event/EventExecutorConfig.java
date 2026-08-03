package com.df4j.xctec.xcms.base.event;

import com.alibaba.ttl.threadpool.TtlExecutors;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 事件异步执行器配置。
 *
 * 使用 TtlExecutors 包装，保证 TenantContext / TraceContext 跨线程透传。
 * 拒绝策略 CallerRunsPolicy：队列满时由调用线程执行，不丢审计事件。
 */
@Configuration
public class EventExecutorConfig {

    @Bean("xcmsEventExecutor")
    public ExecutorService eventExecutor() {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                2,
                4,
                60L, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(512),
                threadFactory("xcms-event-"),
                new ThreadPoolExecutor.CallerRunsPolicy());
        return TtlExecutors.getTtlExecutorService(executor);
    }

    private ThreadFactory threadFactory(String prefix) {
        AtomicInteger counter = new AtomicInteger(0);
        return r -> {
            Thread t = new Thread(r, prefix + counter.getAndIncrement());
            t.setDaemon(true);
            return t;
        };
    }
}
