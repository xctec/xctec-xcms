package com.df4j.xctec.xcms.system.domain.dto;

import com.df4j.xctec.xcms.common.jpa.dto.BaseTreeDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrgUnitDto extends BaseTreeDto<OrgUnitDto> {

    private String orgName;

    private String enableStatus;
}
