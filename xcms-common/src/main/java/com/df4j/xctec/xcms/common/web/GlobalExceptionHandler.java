package com.df4j.xctec.xcms.common.web;

import com.df4j.xctec.xcms.common.core.BizException;
import com.df4j.xctec.xcms.common.core.CommonErrorCode;
import com.df4j.xctec.xcms.common.core.ForbiddenException;
import com.df4j.xctec.xcms.common.core.ResultVo;
import com.df4j.xctec.xcms.common.core.SystemException;
import com.df4j.xctec.xcms.common.core.TraceContext;
import com.df4j.xctec.xcms.common.core.UnauthorizedException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 统一异常处理器：所有异常转为 ResultVo（HTTP 固定 200，业务结果在 errorCode）。
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BizException.class)
    public ResponseEntity<ResultVo<Void>> handleBiz(BizException ex) {
        return ResponseEntity.ok(ResultVo.fail(ex.getErrorCode(), ex.getArgs()));
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ResultVo<Void>> handleUnauthorized(UnauthorizedException ex) {
        ResultVo<Void> vo = ResultVo.fail(ex.getErrorCode(), ex.getArgs());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(vo);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ResultVo<Void>> handleForbidden(ForbiddenException ex) {
        ResultVo<Void> vo = ResultVo.fail(ex.getErrorCode(), ex.getArgs());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(vo);
    }

    @ExceptionHandler(SystemException.class)
    public ResponseEntity<ResultVo<Void>> handleSystem(SystemException ex) {
        return ResponseEntity.ok(ResultVo.fail(ex.getErrorCode(), ex.getArgs()));
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, ConstraintViolationException.class})
    public ResponseEntity<ResultVo<Void>> handleValidation(Exception ex) {
        String msg = ex.getMessage();
        ResultVo<Void> vo = ResultVo.fail(CommonErrorCode.COMMON_VALIDATION_ERROR, msg);
        return ResponseEntity.ok(vo);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResultVo<Void>> handleOther(Exception ex) {
        ResultVo<Void> vo = ResultVo.fail(CommonErrorCode.COMMON_ERROR, ex.getMessage());
        return ResponseEntity.ok(vo);
    }
}
