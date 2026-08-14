package com.df4j.xctec.xcms.system.domain.dto;

import com.df4j.xctec.xcms.common.jpa.dto.BaseTreeDto;
import lombok.Data;

@Data
public class TenantInfoDto extends BaseTreeDto<TenantInfoDto> {

    /** 租户名称 */
    private String tenantName;

    /** 租户状态 */
    private Integer enableStatus;
}
