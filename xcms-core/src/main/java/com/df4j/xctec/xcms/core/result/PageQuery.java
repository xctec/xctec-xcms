package com.df4j.xctec.xcms.core.result;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

import java.io.Serializable;

/**
 * 分页查询入参。pageNo 从 1 开始。
 * orderBy 形如 "createdAt,desc"，由 xcms-web 的 SortResolver 映射为 QueryDSL 表达式，
 * 绝不拼接进 SQL。
 */
@Data
public class PageQuery implements Serializable {

    @Min(value = 1, message = "页码最小为 1")
    private Integer pageNo = 1;

    @Min(value = 1, message = "每页条数最小为 1")
    @Max(value = 200, message = "每页条数最大为 200")
    private Integer pageSize = 20;

    /** 形如 createdAt,desc，由 SortResolver 映射为 QueryDSL 表达式 */
    private String orderBy;

    public long offset() {
        return (long) (pageNo - 1) * pageSize;
    }
}
