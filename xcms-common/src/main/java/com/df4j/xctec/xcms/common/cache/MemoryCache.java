package com.df4j.xctec.xcms.common.cache;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * 统一内存缓存抽象（自研）。默认实现为 Caffeine 风格自动过期。
 */
public interface MemoryCache {

    <V> V get(String key, Class<V> type);

    <V> Optional<V> getIfPresent(String key, Class<V> type);

    void put(String key, Object value);

    void put(String key, Object value, long ttl, TimeUnit unit);

    boolean putIfAbsent(String key, Object value);

    void invalidate(String key);

    void invalidateAll();

    <V> V computeIfAbsent(String key, Class<V> type, Function<String, V> mapping);
}
