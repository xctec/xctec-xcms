package com.df4j.xctec.xcms.core.exception;

import lombok.Getter;

public class BizException extends RuntimeException {

    @Getter
    private String errorNo;

    public BizException(String errorNo, String message) {
        super(message);
        this.errorNo = errorNo;
    }

    public BizException(String errorNo, String message, Throwable cause) {
        super(message, cause);
        this.errorNo = errorNo;
    }

    public static BizException of(String errorNo, String errorMsg) {
        return new BizException(errorNo, errorMsg);
    }

    public static BizException of(String errorNo, String errorMsg, Throwable cause) {
        return new BizException(errorNo, errorMsg, cause);
    }
}
