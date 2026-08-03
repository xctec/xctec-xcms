package com.df4j.xctec.xcms.core.result;

import com.df4j.xctec.xcms.core.trace.TraceContext;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

/**
 * 统一返回体。errorNo == null 表示成功。
 * 配合 @JsonInclude(NON_NULL)，成功响应中不出现错误字段。
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ResultVo<T>(
        String errorNo,
        String errorMsg,
        T data,
        String traceId,
        Long timestamp
) implements Serializable {

    public static <T> ResultVo<T> ok() {
        return ok(null);
    }

    public static <T> ResultVo<T> ok(T data) {
        return new ResultVo<>(null, null, data,
                TraceContext.currentTraceId(), System.currentTimeMillis());
    }

    public static <T> ResultVo<T> fail(ErrorCode code, Object... args) {
        return new ResultVo<>(code.code(), code.format(args), null,
                TraceContext.currentTraceId(), System.currentTimeMillis());
    }

    public static <T> ResultVo<T> fail(String errorNo, String errorMsg) {
        return new ResultVo<>(errorNo, errorMsg, null,
                TraceContext.currentTraceId(), System.currentTimeMillis());
    }

    public boolean isSuccess() {
        return errorNo == null;
    }
}
