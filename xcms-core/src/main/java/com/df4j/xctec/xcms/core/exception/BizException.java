package com.df4j.xctec.xcms.core.exception;

import com.df4j.xctec.xcms.core.result.ErrorCode;

/**
 * 业务异常。携带 ErrorCode，统一由 GlobalExceptionHandler 转换为 ResultVo。
 */
public class BizException extends RuntimeException {

    private final transient ErrorCode errorCode;
    private final transient Object[] args;

    public BizException(ErrorCode errorCode, Object... args) {
        super(errorCode.format(args));
        this.errorCode = errorCode;
        this.args = args;
    }

    public BizException(ErrorCode errorCode, Throwable cause, Object... args) {
        super(errorCode.format(args), cause);
        this.errorCode = errorCode;
        this.args = args;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public Object[] getArgs() {
        return args;
    }
}
