package com.df4j.xctec.xcms.base.cache;

import com.df4j.xctec.xcms.core.cache.MemoryCache;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * 基于 ConcurrentHashMap 的内存缓存默认实现。
 *
 * 支持 TTL 过期与容量上限（LRU 淘汰）。
 * 当前为开发阶段的简单实现，后续可替换为 Caffeine / Redis。
 */
@Component
@ConditionalOnMissingBean(MemoryCache.class)
public class DefaultMemoryCache<K, V> implements MemoryCache<K, V> {

    private final Map<K, Entry<V>> store = new ConcurrentHashMap<>();
    private final int maxSize;

    public DefaultMemoryCache() {
        this(10000);
    }

    public DefaultMemoryCache(int maxSize) {
        this.maxSize = maxSize;
    }

    @Override
    public V get(K key) {
        Entry<V> entry = store.get(key);
        if (entry == null) {
            return null;
        }
        if (entry.isExpired()) {
            store.remove(key);
            return null;
        }
        return entry.value;
    }

    @Override
    public V get(K key, Function<K, V> loader) {
        V value = get(key);
        if (value != null) {
            return value;
        }
        value = loader.apply(key);
        if (value != null) {
            put(key, value);
        }
        return value;
    }

    @Override
    public void put(K key, V value) {
        ensureCapacity();
        store.put(key, new Entry<>(value, 0));
    }

    @Override
    public void put(K key, V value, Duration ttl) {
        ensureCapacity();
        long expireAt = ttl == null ? 0 : System.currentTimeMillis() + ttl.toMillis();
        store.put(key, new Entry<>(value, expireAt));
    }

    @Override
    public void evict(K key) {
        store.remove(key);
    }

    @Override
    public void clear() {
        store.clear();
    }

    @Override
    public int size() {
        return store.size();
    }

    private void ensureCapacity() {
        if (maxSize > 0 && store.size() >= maxSize) {
            K keyToRemove = store.entrySet().stream()
                    .findFirst()
                    .map(Map.Entry::getKey)
                    .orElse(null);
            if (keyToRemove != null) {
                store.remove(keyToRemove);
            }
        }
    }

    private static class Entry<V> {
        final V value;
        final long expireAt; // 0 表示永不过期

        Entry(V value, long expireAt) {
            this.value = value;
            this.expireAt = expireAt;
        }

        boolean isExpired() {
            return expireAt > 0 && System.currentTimeMillis() > expireAt;
        }
    }
}
