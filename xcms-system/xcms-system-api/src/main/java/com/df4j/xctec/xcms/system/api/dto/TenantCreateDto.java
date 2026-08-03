package com.df4j.xctec.xcms.system.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * 租户创建入参。
 */
@Data
public class TenantCreateDto implements Serializable {

    /** 父租户 id，根租户传 0 */
    @NotNull(message = "父租户不能为空")
    private Long parentId = 0L;

    @NotBlank(message = "租户编码不能为空")
    @Size(max = 64, message = "租户编码最长 64 位")
    private String code;

    @NotBlank(message = "租户名称不能为空")
    @Size(max = 128, message = "租户名称最长 128 位")
    private String name;

    /** 排序号，同级展示顺序 */
    private Integer sortNo;

    /** 状态：1 正常 0 停用 */
    private Integer status = 1;
}
