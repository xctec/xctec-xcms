package com.df4j.xctec.xcms.common.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 分页结果。
 */
public class PageVo<T> implements Serializable {

    private long total;
    private int page;
    private int size;
    private List<T> list = new ArrayList<>();

    public static <T> PageVo<T> of(long total, int page, int size, List<T> list) {
        PageVo<T> vo = new PageVo<>();
        vo.total = total;
        vo.page = page;
        vo.size = size;
        vo.list = list == null ? new ArrayList<>() : list;
        return vo;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }
}
