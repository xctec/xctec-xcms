package com.df4j.xctec.xcms.common.core;

/**
 * 参数校验异常。
 */
public class ValidationException extends BizException {
    public ValidationException(ErrorCode errorCode, Object... args) {
        super(errorCode, args);
    }
}
