package com.df4j.xctec.xcms.core.exception;

import com.df4j.xctec.xcms.core.result.ErrorCode;

/**
 * 越权 / 禁止访问异常。
 */
public class ForbiddenException extends BizException {
    public ForbiddenException(ErrorCode errorCode, Object... args) {
        super(errorCode, args);
    }
}
