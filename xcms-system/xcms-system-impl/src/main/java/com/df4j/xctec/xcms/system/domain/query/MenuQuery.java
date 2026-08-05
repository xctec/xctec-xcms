package com.df4j.xctec.xcms.system.domain.query;

import com.df4j.xctec.xcms.core.vo.PageQuery;
import lombok.Data;

@Data
public class MenuQuery extends PageQuery {

    private String menuName;

    private String menuType;

    private String permission;

    private String enableStatus;
}
