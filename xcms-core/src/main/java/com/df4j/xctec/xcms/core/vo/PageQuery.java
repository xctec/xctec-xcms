package com.df4j.xctec.xcms.core.vo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageQuery {

    private Integer pageNo = 1;
    private Integer pageSize = 20;
    private String orderBy;
    private String order;
    private String keyword;

    public long offset() {
        return (long) (pageNo - 1) * pageSize;
    }
}
