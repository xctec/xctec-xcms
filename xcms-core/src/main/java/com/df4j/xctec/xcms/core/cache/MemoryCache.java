package com.df4j.xctec.xcms.core.cache;

import java.time.Duration;
import java.util.function.Function;

/**
 * 统一内存缓存抽象。
 * 接口定义于 xcms-core，当前默认实现在 xcms-base（DefaultMemoryCache），
 * 后续可按同一接口扩展 Caffeine / Redis 实现，业务代码零改动。
 */
public interface MemoryCache<K, V> {

    V get(K key);

    V get(K key, Function<K, V> loader);

    void put(K key, V value);

    void put(K key, V value, Duration ttl);

    void evict(K key);

    void clear();

    int size();
}
