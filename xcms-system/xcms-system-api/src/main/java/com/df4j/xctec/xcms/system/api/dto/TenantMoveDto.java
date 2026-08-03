package com.df4j.xctec.xcms.system.api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * 租户子树迁移入参（改父）。
 */
@Data
public class TenantMoveDto implements Serializable {

    /** 待移动租户 id */
    @NotNull(message = "租户 id 不能为空")
    private Long id;

    /** 新父租户 id，根租户传 0 */
    @NotNull(message = "目标父租户不能为空")
    private Long parentId;
}
