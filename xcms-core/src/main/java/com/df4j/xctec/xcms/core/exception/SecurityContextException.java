package com.df4j.xctec.xcms.core.exception;

/**
 * 安全上下文相关的业务异常（{@link BizException} 的子类）。
 *
 * <p>用于“缺少租户/登录用户上下文”“未认证即访问受保护资源”等越权/鉴权语义，
 * 由全局异常处理器统一转换为业务响应（带 errorNo）。
 *
 * <p>与裸 {@link BizException} 相比，本类可在类型上被单独捕获，便于做 401/403 之类的专门处理。
 */
public class SecurityContextException extends BizException {

    public static final String NO_CONTEXT = "XCMS.NO_CONTEXT";
    public static final String NO_TENANT = "XCMS.NO_TENANT";
    public static final String NO_TARGET_TENANT = "XCMS.NO_TARGET_TENANT";
    public static final String NO_LOGIN = "XCMS.NO_LOGIN";

    public SecurityContextException(String errorNo, String message) {
        super(errorNo, message);
    }

    public SecurityContextException(String errorNo, String message, Throwable cause) {
        super(errorNo, message, cause);
    }

    public static SecurityContextException noContext() {
        return new SecurityContextException(NO_CONTEXT, "当前线程不存在安全上下文");
    }

    public static SecurityContextException noTenant() {
        return new SecurityContextException(NO_TENANT, "当前上下文缺少租户信息");
    }

    public static SecurityContextException noTargetTenant() {
        return new SecurityContextException(NO_TARGET_TENANT, "提权目标租户不能为空");
    }

    public static SecurityContextException noLogin() {
        return new SecurityContextException(NO_LOGIN, "当前上下文缺少登录用户信息");
    }
}
