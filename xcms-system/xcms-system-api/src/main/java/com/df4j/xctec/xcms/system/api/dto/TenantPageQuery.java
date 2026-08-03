package com.df4j.xctec.xcms.system.api.dto;

import com.df4j.xctec.xcms.core.result.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 租户分页查询入参。继承通用 {@link PageQuery}，额外携带过滤条件。
 * orderBy 由 SortResolver 统一映射为 QueryDSL 表达式，绝不拼接进 SQL。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TenantPageQuery extends PageQuery implements Serializable {

    /** 编码模糊匹配 */
    private String code;

    /** 名称模糊匹配 */
    private String name;

    /** 状态过滤：1 正常 0 停用，null 表示不过滤 */
    private Integer status;
}
