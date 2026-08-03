package com.df4j.xctec.xcms.common.jpa.id;

import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 号段模式 ID：每次从数据库取一段号（如 1000 个）缓存在内存，用完再取。
 * 依赖 sys_id_segment 表，通过 JdbcTemplate 原子更新 max_id。
 */
public class SegmentIdStrategy implements IdGenerateStrategy {

    private final JdbcTemplate jdbcTemplate;
    private final int step;
    private final Map<String, AtomicLong> current = new ConcurrentHashMap<>();
    private final Map<String, Long> max = new ConcurrentHashMap<>();

    public SegmentIdStrategy(JdbcTemplate jdbcTemplate, int step) {
        this.jdbcTemplate = jdbcTemplate;
        this.step = Math.max(1, step);
    }

    @Override
    public long nextId(Class<?> entityClass) {
        String key = entityClass == null ? "default" : entityClass.getSimpleName();
        AtomicLong cur = current.computeIfAbsent(key, k -> new AtomicLong(load(k)));
        Long mx = max.get(key);
        long v = cur.incrementAndGet();
        if (v > mx) {
            synchronized (this) {
                if (cur.get() >= max.get(key)) {
                    cur.set(load(key));
                }
                v = cur.incrementAndGet();
            }
        }
        return v;
    }

    private long load(String key) {
        jdbcTemplate.update(
                "UPDATE sys_id_segment SET max_id = max_id + ? WHERE biz_key = ?", step, key);
        Long newMax = jdbcTemplate.queryForObject(
                "SELECT max_id FROM sys_id_segment WHERE biz_key = ?", Long.class, key);
        if (newMax == null) {
            throw new IllegalStateException("id segment not initialized for key: " + key);
        }
        long start = newMax - step;
        max.put(key, newMax);
        return start;
    }

    @Override
    public String name() {
        return "segment";
    }
}
