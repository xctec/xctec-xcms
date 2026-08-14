package com.df4j.xctec.xcms.system.api.query;

import com.df4j.xctec.xcms.core.vo.PageQuery;
import lombok.Data;

import java.util.Set;

/**
 * 用户菜单查询条件，携带当前用户上下文，供权限过滤使用。
 */
@Data
public class UserMenuQuery extends PageQuery {

    /** 租户标识，菜单按租户隔离（必填）。 */
    private Long tenantId;

    /** 当前用户标识，预留用于后续 RBAC 关联过滤。 */
    private Long userId;

    /** 当前用户角色编码集合，用于权限标识匹配（开放菜单不受限）。 */
    private Set<String> roleCodes;

    private String menuType;

    private String enableStatus;

    private String visibleStatus;
}
