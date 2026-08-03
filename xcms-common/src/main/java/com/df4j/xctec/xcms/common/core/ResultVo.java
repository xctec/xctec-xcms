package com.df4j.xctec.xcms.common.core;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.util.function.Supplier;

/**
 * 统一返回体。errorCode == null 表示成功。
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResultVo<T> implements Serializable {

    private String errorCode;
    private String errorMsg;
    private String traceId;
    private T data;

    public static <T> ResultVo<T> success(T data) {
        ResultVo<T> vo = new ResultVo<>();
        vo.data = data;
        vo.traceId = TraceContext.getTraceId();
        return vo;
    }

    public static <T> ResultVo<T> success() {
        return success(null);
    }

    public static <T> ResultVo<T> fail(ErrorCode errorCode, Object... args) {
        ResultVo<T> vo = new ResultVo<>();
        vo.errorCode = errorCode.code();
        vo.errorMsg = errorCode.format(args);
        vo.traceId = TraceContext.getTraceId();
        return vo;
    }

    public static <T> ResultVo<T> fail(ErrorCode errorCode, Throwable cause, Object... args) {
        ResultVo<T> vo = fail(errorCode, args);
        vo.errorMsg = errorCode.format(args) + "：" + cause.getMessage();
        return vo;
    }

    public boolean isSuccess() {
        return errorCode == null;
    }

    public T orElseThrow(Supplier<? extends RuntimeException> exSupplier) {
        if (isSuccess()) {
            return data;
        }
        throw exSupplier.get();
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
