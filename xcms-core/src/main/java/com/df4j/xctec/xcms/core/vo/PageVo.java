package com.df4j.xctec.xcms.core.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageVo<T> {

    private static final PageVo<?> EMPTY = new PageVo<>(1, 20, 0L, null);

    private Integer pageNum;
    private Integer pageSize;
    private Long total;
    private List<T> list;


    @SuppressWarnings("unchecked")
    public static <D> PageVo<D> empty() {
        return (PageVo<D>) EMPTY;
    }

    public static <D> PageVo<D> of(Integer pageNum, Integer pageSize, Long total, List<D> list) {
        return new PageVo<>(pageNum, pageSize, total, list);
    }
}
