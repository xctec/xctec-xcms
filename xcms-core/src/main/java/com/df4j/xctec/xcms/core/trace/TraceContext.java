package com.df4j.xctec.xcms.core.trace;

import com.alibaba.ttl.TransmittableThreadLocal;

import java.util.UUID;

/**
 * 链路追踪上下文（基于 TTL 透传）。
 */
public final class TraceContext {

    private static final ThreadLocal<String> TRACE_ID = new TransmittableThreadLocal<>();

    private TraceContext() {
    }

    public static String currentTraceId() {
        String id = TRACE_ID.get();
        if (id == null) {
            id = UUID.randomUUID().toString().replace("-", "");
            TRACE_ID.set(id);
        }
        return id;
    }

    public static void setTraceId(String traceId) {
        TRACE_ID.set(traceId);
    }

    public static void clear() {
        TRACE_ID.remove();
    }
}
