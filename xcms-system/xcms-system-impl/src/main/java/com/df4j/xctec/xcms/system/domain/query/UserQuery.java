package com.df4j.xctec.xcms.system.domain.query;

import com.df4j.xctec.xcms.core.vo.PageQuery;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserQuery extends PageQuery {

    private String username;

    private String nickname;

    private String mobile;

    private Long orgUnitId;

    private String enableStatus;
}
