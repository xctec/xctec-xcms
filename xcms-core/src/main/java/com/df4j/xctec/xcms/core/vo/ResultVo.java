package com.df4j.xctec.xcms.core.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResultVo<T> {
    private String errorNo = "0";
    private String errorMsg = "success";
    private T data;

    public ResultVo(String errorNo, String errorMsg) {
        this.errorNo = errorNo;
        this.errorMsg = errorMsg;
    }

    public ResultVo(T data) {
        this.data = data;
    }
}
