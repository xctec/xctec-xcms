package com.df4j.xctec.xcms.common.core;

/**
 * 未认证异常。
 */
public class UnauthorizedException extends BizException {
    public UnauthorizedException(ErrorCode errorCode, Object... args) {
        super(errorCode, args);
    }
}
