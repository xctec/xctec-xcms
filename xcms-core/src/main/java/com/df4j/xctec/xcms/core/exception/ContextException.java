package com.df4j.xctec.xcms.core.exception;

/**
 * 安全上下文相关的业务异常（{@link BizException} 的子类）。
 *
 * <p>用于“缺少租户/登录用户上下文”“未认证即访问受保护资源”等越权/鉴权语义，
 * 由全局异常处理器统一转换为业务响应（带 errorNo）。
 *
 * <p>与裸 {@link BizException} 相比，本类可在类型上被单独捕获，便于做 401/403 之类的专门处理。
 */
public class ContextException extends BizException {

    public static final String NO_CONTEXT = "XCMS.NO_CONTEXT";
    public ContextException(String errorNo, String message) {
        super(errorNo, message);
    }

    public ContextException(String errorNo, String message, Throwable cause) {
        super(errorNo, message, cause);
    }

    public static ContextException noContext() {
        return new ContextException(NO_CONTEXT, "当前线程不存在安全上下文");
    }
 }
