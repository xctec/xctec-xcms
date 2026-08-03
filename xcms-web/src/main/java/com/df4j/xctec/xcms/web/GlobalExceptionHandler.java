package com.df4j.xctec.xcms.web;

import com.df4j.xctec.xcms.core.exception.BizException;
import com.df4j.xctec.xcms.core.exception.ForbiddenException;
import com.df4j.xctec.xcms.core.exception.SystemException;
import com.df4j.xctec.xcms.core.exception.UnauthorizedException;
import com.df4j.xctec.xcms.core.result.CommonErrorCode;
import com.df4j.xctec.xcms.core.result.ResultVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * 全局异常处理器。
 *
 * HTTP 状态码统一 200，业务结果由 ResultVo.errorNo 表达。
 * 异常分类仅用于日志分级，不影响 HTTP 状态码。
 * 系统异常不向前端暴露 ex.getMessage()，返回固定提示。
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BizException.class)
    public ResponseEntity<ResultVo<Void>> handleBiz(BizException e) {
        log.warn("biz error: {} - {}", e.getErrorCode().code(), e.getMessage());
        return ResponseEntity.ok(ResultVo.fail(e.getErrorCode(), e.getArgs()));
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ResultVo<Void>> handleUnauthorized(UnauthorizedException e) {
        log.info("unauthorized: {} - {}", e.getErrorCode().code(), e.getMessage());
        return ResponseEntity.ok(ResultVo.fail(e.getErrorCode(), e.getArgs()));
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ResultVo<Void>> handleForbidden(ForbiddenException e) {
        log.warn("forbidden: {} - {}", e.getErrorCode().code(), e.getMessage());
        return ResponseEntity.ok(ResultVo.fail(e.getErrorCode(), e.getArgs()));
    }

    @ExceptionHandler(SystemException.class)
    public ResponseEntity<ResultVo<Void>> handleSystem(SystemException e) {
        log.error("system error: {} - {}", e.getErrorCode().code(), e.getMessage());
        return ResponseEntity.ok(ResultVo.fail(e.getErrorCode(), e.getArgs()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResultVo<Void>> handleValid(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .map(f -> f.getField() + ": " + f.getDefaultMessage())
                .collect(Collectors.joining("; "));
        log.warn("validation error: {}", msg);
        return ResponseEntity.ok(ResultVo.fail(CommonErrorCode.VALIDATION_FAILED, msg));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResultVo<Void>> handleUnknown(Exception e) {
        log.error("unexpected system error", e);
        return ResponseEntity.ok(ResultVo.fail(CommonErrorCode.SYSTEM_ERROR, "系统繁忙，请稍后重试"));
    }
}
