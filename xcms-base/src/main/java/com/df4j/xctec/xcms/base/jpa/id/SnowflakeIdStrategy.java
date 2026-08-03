package com.df4j.xctec.xcms.base.jpa.id;

import com.df4j.xctec.xcms.core.id.IdGenerateStrategy;

import java.net.NetworkInterface;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.time.Instant;

/**
 * 雪花算法 ID。41bit 时间戳 + 10bit 工作节点 + 12bit 序列。
 *
 * 默认节点号 = 进程内随机 + 网卡哈希，冲突概率极低。
 * 支持配置 workerId / datacenterId 显式指定（多实例部署时必须分配不同 workerId）。
 * 处理时钟回拨：检测到回拨时等待至 lastTs。
 */
public class SnowflakeIdStrategy implements IdGenerateStrategy {

    private static final long EPOCH = 1700000000000L;
    private static final long NODE_BITS = 10L;
    private static final long SEQ_BITS = 12L;
    private static final long MAX_NODE = (1L << NODE_BITS) - 1;
    private static final long MAX_SEQ = (1L << SEQ_BITS) - 1;

    private final long nodeId;
    private long lastTs = -1L;
    private long sequence = 0L;

    public SnowflakeIdStrategy() {
        this(defaultNodeId());
    }

    public SnowflakeIdStrategy(long workerId, long datacenterId) {
        // workerId 5bit + datacenterId 5bit 拼成 10bit nodeId
        this(((datacenterId & 0x1F) << 5) | (workerId & 0x1F));
    }

    public SnowflakeIdStrategy(long nodeId) {
        if (nodeId < 0 || nodeId > MAX_NODE) {
            throw new IllegalArgumentException("nodeId out of range [0," + MAX_NODE + "]: " + nodeId);
        }
        this.nodeId = nodeId;
    }

    private static long defaultNodeId() {
        try {
            byte[] mac = NetworkInterface.networkInterfaces()
                    .findFirst()
                    .map(ni -> {
                        try {
                            return ni.getHardwareAddress();
                        } catch (Exception e) {
                            return null;
                        }
                    })
                    .orElse(null);
            long h = new SecureRandom().nextInt(1024);
            if (mac != null) {
                ByteBuffer bb = ByteBuffer.wrap(new byte[8]);
                bb.put(mac, 0, Math.min(mac.length, 6));
                h = (h ^ bb.getLong()) & MAX_NODE;
            }
            return h & MAX_NODE;
        } catch (Exception e) {
            return new SecureRandom().nextInt((int) (MAX_NODE + 1));
        }
    }

    @Override
    public synchronized Long nextId() {
        long ts = Instant.now().toEpochMilli() - EPOCH;
        // 时钟回拨处理：等待至 lastTs
        if (ts < lastTs) {
            ts = waitNextMillis(lastTs);
        }
        if (ts == lastTs) {
            sequence = (sequence + 1) & MAX_SEQ;
            if (sequence == 0) {
                ts = waitNextMillis(ts);
            }
        } else {
            sequence = 0;
        }
        lastTs = ts;
        return (ts << (NODE_BITS + SEQ_BITS)) | (nodeId << SEQ_BITS) | sequence;
    }

    private long waitNextMillis(long prevTs) {
        long t;
        do {
            t = Instant.now().toEpochMilli() - EPOCH;
        } while (t <= prevTs);
        return t;
    }

    @Override
    public String name() {
        return "snowflake";
    }
}
