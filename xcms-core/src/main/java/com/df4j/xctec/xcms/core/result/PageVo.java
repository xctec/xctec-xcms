package com.df4j.xctec.xcms.core.result;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

/**
 * 分页结果。作为 ResultVo 的 data 载体，不与 ResultVo 平级。
 * pageNo 对外从 1 开始，偏移换算封装在 of() 内部。
 */
public record PageVo<T>(
        List<T> records,
        Long total,
        Integer pageNo,
        Integer pageSize,
        Integer totalPages
) implements Serializable {

    public PageVo {
        if (records == null) {
            records = Collections.emptyList();
        }
    }

    public static <T> PageVo<T> of(List<T> records, Long total, Integer pageNo, Integer pageSize) {
        int totalPages = (pageSize == null || pageSize <= 0)
                ? 0 : (int) Math.ceil((double) total / pageSize);
        return new PageVo<>(records, total, pageNo, pageSize, totalPages);
    }

    public static <T> PageVo<T> empty(Integer pageNo, Integer pageSize) {
        return new PageVo<>(Collections.emptyList(), 0L, pageNo, pageSize, 0);
    }

    /**
     * 从 Spring Data Page 转换。pageNo 对外从 1 开始。
     * 此方法依赖 spring-data-commons，xcms-core 不直接引入 Spring Data，
     * 故放在 xcms-base 或 impl 中调用 of(records, total, pageNo, pageSize)。
     * 此处仅保留 records 级构造，Spring Data Page 的适配由 impl 完成。
     */
    public static <E, T> PageVo<T> of(List<E> records, Long total, Integer pageNo, Integer pageSize,
                                      Function<E, T> converter) {
        List<T> converted = records.stream().map(converter).toList();
        return of(converted, total, pageNo, pageSize);
    }
}
