package com.df4j.xctec.xcms.common.core;

import java.io.Serializable;

/**
 * 分页查询入参。page 从 1 开始。
 */
public class PageQuery implements Serializable {

    public static final int DEFAULT_PAGE = 1;
    public static final int DEFAULT_SIZE = 20;
    public static final int MAX_SIZE = 1000;

    private int page = DEFAULT_PAGE;
    private int size = DEFAULT_SIZE;

    public PageQuery() {
    }

    public PageQuery(int page, int size) {
        this.page = page;
        this.size = size;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = Math.max(DEFAULT_PAGE, page);
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = Math.min(MAX_SIZE, Math.max(1, size));
    }

    public long offset() {
        return (long) (getPage() - 1) * getSize();
    }

    public int limit() {
        return getSize();
    }
}
