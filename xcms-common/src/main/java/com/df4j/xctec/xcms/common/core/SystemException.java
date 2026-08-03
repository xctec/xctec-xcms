package com.df4j.xctec.xcms.common.core;

/**
 * 系统级异常（非业务、通常不可预期）。
 */
public class SystemException extends BizException {
    public SystemException(ErrorCode errorCode, Object... args) {
        super(errorCode, args);
    }

    public SystemException(ErrorCode errorCode, Throwable cause, Object... args) {
        super(errorCode, cause, args);
    }
}
