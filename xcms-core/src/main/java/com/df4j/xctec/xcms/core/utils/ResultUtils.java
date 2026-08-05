package com.df4j.xctec.xcms.core.utils;

import com.df4j.xctec.xcms.core.vo.ResultVo;

public class ResultUtils {

    public static <T> ResultVo<T> success() {
        return new ResultVo<>();
    }

    public static <T> ResultVo<T> success(T data) {
        return new ResultVo<>(data);
    }

    public static <T> ResultVo<T> error(String errorNo, String errorMsg) {
        return new ResultVo<>(errorNo, errorMsg);
    }

    public static <T> ResultVo<T> error(String errorNo, String errorMsg, T data) {
        return new ResultVo<>(errorNo, errorMsg, data);
    }
}
