package com.df4j.xctec.xcms.system.domain.dto;

import com.df4j.xctec.xcms.common.jpa.dto.BaseTreeDto;
import lombok.Data;

@Data
public class OrgUnitDto extends BaseTreeDto<OrgUnitDto> {

    private String orgName;

    private String enableStatus;
}
