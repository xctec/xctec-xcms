package com.df4j.xctec.xcms.auth.api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * 踢线入参（按用户 / 按租户批量失效在线令牌）。
 */
@Data
public class KickDto implements Serializable {

    /** 目标用户 id，与 tenantId 二选一 */
    private Long userId;

    /** 目标租户 id，与 userId 二选一 */
    private Long tenantId;
}
