package com.df4j.xctec.xcms.core.exception;

import com.df4j.xctec.xcms.core.result.ErrorCode;

/**
 * 未认证异常。
 */
public class UnauthorizedException extends BizException {
    public UnauthorizedException(ErrorCode errorCode, Object... args) {
        super(errorCode, args);
    }
}
