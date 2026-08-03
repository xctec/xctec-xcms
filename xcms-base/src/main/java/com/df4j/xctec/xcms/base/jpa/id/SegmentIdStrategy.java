package com.df4j.xctec.xcms.base.jpa.id;

import com.df4j.xctec.xcms.core.id.IdGenerateStrategy;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 号段模式 ID：每次从数据库取一段号（如 1000 个）缓存在内存，用完再取。
 * 依赖 sys_id_segment 表，通过 JdbcTemplate 原子更新 max_id。
 *
 * 表结构：sys_id_segment(biz_key VARCHAR PK, max_id BIGINT)
 * 多实例下通过行锁保证号段不重叠。
 */
public class SegmentIdStrategy implements IdGenerateStrategy {

    private final JdbcTemplate jdbcTemplate;
    private final int step;

    private volatile Segment current;

    public SegmentIdStrategy(JdbcTemplate jdbcTemplate, int step) {
        this.jdbcTemplate = jdbcTemplate;
        this.step = Math.max(1, step);
    }

    @Override
    public synchronized Long nextId() {
        if (current == null || !current.hasNext()) {
            current = loadNextSegment();
        }
        return current.next();
    }

    private Segment loadNextSegment() {
        String key = "default";
        // 原子更新：max_id += step，返回更新后的 max_id
        jdbcTemplate.update(
                "UPDATE sys_id_segment SET max_id = max_id + ? WHERE biz_key = ?",
                step, key);
        Long newMax = jdbcTemplate.queryForObject(
                "SELECT max_id FROM sys_id_segment WHERE biz_key = ?", Long.class, key);
        if (newMax == null) {
            throw new IllegalStateException("id segment not initialized for key: " + key
                    + ", ensure sys_id_segment has a row with biz_key='default'");
        }
        long start = newMax - step + 1;
        return new Segment(start, newMax);
    }

    @Override
    public String name() {
        return "segment";
    }

    /**
     * 号段内存缓冲：[start, max] 闭区间。
     */
    private static class Segment {
        private final AtomicLong cursor;
        private final long max;

        Segment(long start, long max) {
            this.cursor = new AtomicLong(start);
            this.max = max;
        }

        long next() {
            return cursor.getAndIncrement();
        }

        boolean hasNext() {
            return cursor.get() <= max;
        }
    }
}
