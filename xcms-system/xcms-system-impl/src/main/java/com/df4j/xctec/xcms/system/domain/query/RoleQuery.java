package com.df4j.xctec.xcms.system.domain.query;

import com.df4j.xctec.xcms.core.vo.PageQuery;
import lombok.Data;

@Data
public class RoleQuery extends PageQuery {

    private String roleCode;

    private String roleName;

}
