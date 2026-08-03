package com.df4j.xctec.xcms.core.exception;

import com.df4j.xctec.xcms.core.result.ErrorCode;

/**
 * 参数校验异常。
 */
public class ValidationException extends BizException {
    public ValidationException(ErrorCode errorCode, Object... args) {
        super(errorCode, args);
    }
}
