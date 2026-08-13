package com.df4j.xctec.xcms.common.web.exception;

import com.df4j.xctec.xcms.core.exception.BizException;
import com.df4j.xctec.xcms.core.utils.ResultUtils;
import com.df4j.xctec.xcms.core.vo.ResultVo;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * 全局异常处理器（横切基础设施，下沉至 common 层）。
 *
 * <p>仅对进入 DispatcherServlet 后到达 Controller 抛出的异常生效；
 * Security 过滤器链内的认证/授权异常由 {@code XcmsAuthenticationEntryPoint} /
 * {@code XcmsAccessDeniedHandler} 经 {@code ResponseUtils} 以相同 {@link ResultVo} 结构兜底，
 * 二者共用 {@code errorNo/errorMsg/data} 约定，前端无需两套解析。
 *
 * <p>约定：业务/上下文异常走 errorNo 通道且 HTTP 200（与 Security Handler 风格一致）；
 * 参数校验/系统异常使用真实 HTTP 状态码，利于网关与监控。
 *
 * <p>本处理器仅依赖 common 既有能力（spring-web + core），不额外引入依赖。
 * 若后续需要覆盖 {@code ConstraintViolationException}/{@code NoHandlerFoundException} 等，
 * 再按需补充对应依赖。
 */
@Slf4j
@RestControllerAdvice
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class GlobalExceptionHandler {

    /** 业务异常（含 ContextException 子类）：HTTP 200 + errorNo，与 Security Handler 风格一致。 */
    @ExceptionHandler(BizException.class)
    public ResponseEntity<ResultVo<?>> handleBizException(BizException ex, HttpServletRequest request) {
        log.warn("业务异常 [{}] {} - {}", ex.getErrorNo(), ex.getMessage(), request.getRequestURI());
        return ResponseEntity.ok(ResultUtils.error(ex.getErrorNo(), ex.getMessage()));
    }

    /** &#064;RequestBody  校验失败（Jakarta Bean Validation）。 */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResultVo<?>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        String detail = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResultUtils.error("400", "参数校验失败: " + detail));
    }

    /** 表单/Bean 整体绑定校验失败。 */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ResultVo<?>> handleBindException(BindException ex) {
        String detail = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResultUtils.error("400", "参数校验失败: " + detail));
    }

    /** 请求体无法解析（JSON 格式错误等）。 */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ResultVo<?>> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResultUtils.error("400", "请求体解析失败"));
    }

    /** 缺少必填请求参数。 */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ResultVo<?>> handleMissingParameter(MissingServletRequestParameterException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResultUtils.error("400", "缺少必填参数: " + ex.getParameterName()));
    }

    /** 兜底：系统未预期异常，不向客户端暴露堆栈细节。 */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResultVo<?>> handleException(Exception ex, HttpServletRequest request) {
        log.error("系统异常 {} - {}", request.getRequestURI(), ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResultUtils.error("500", "系统繁忙，请稍后重试"));
    }
}
