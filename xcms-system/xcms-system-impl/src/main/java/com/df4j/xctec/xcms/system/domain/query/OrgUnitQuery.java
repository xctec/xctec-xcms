package com.df4j.xctec.xcms.system.domain.query;

import com.df4j.xctec.xcms.core.vo.PageQuery;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrgUnitQuery extends PageQuery {

    private String orgName;

    private String enableStatus;
}
