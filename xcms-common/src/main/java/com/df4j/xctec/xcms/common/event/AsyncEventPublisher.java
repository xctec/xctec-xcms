package com.df4j.xctec.xcms.common.event;

import com.alibaba.ttl.threadpool.TtlExecutors;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 异步事件发布器。
 * - 使用 TTL 包装的执行器，保证 TraceId / TenantContext 跨线程透传。
 * - publish：直接提交到线程池。
 * - publishAfterCommit：注册到 Spring 事务同步器，事务成功提交后才投递。
 */
public class AsyncEventPublisher implements EventPublisher {

    private final Executor executor;

    public AsyncEventPublisher(Executor executor) {
        this.executor = TtlExecutors.getTtlExecutor(executor);
    }

    public AsyncEventPublisher() {
        ExecutorService delegate = new ThreadPoolExecutor(
                4, 16, 60L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(1024),
                r -> {
                    Thread t = new Thread(r, "xcms-event-");
                    t.setDaemon(true);
                    return t;
                },
                new ThreadPoolExecutor.CallerRunsPolicy());
        this.executor = TtlExecutors.getTtlExecutor(delegate);
    }

    @Override
    public void publish(AbstractEvent event) {
        executor.execute(() -> EventBus.dispatch(event));
    }

    @Override
    public void publishAfterCommit(AbstractEvent event) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(
                    new TransactionSynchronization() {
                        @Override
                        public void afterCommit() {
                            executor.execute(() -> EventBus.dispatch(event));
                        }
                    });
        } else {
            EventBus.dispatch(event);
        }
    }
}
