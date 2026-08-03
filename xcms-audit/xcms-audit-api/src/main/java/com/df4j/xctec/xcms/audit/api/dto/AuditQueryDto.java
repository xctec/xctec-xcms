package com.df4j.xctec.xcms.audit.api.dto;

import com.df4j.xctec.xcms.core.result.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 审计日志分页查询入参。继承通用 {@link PageQuery}。
 * orderBy 由 SortResolver 统一映射为 QueryDSL 表达式，绝不拼接进 SQL。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AuditQueryDto extends PageQuery implements Serializable {

    /** 模块过滤，如 system / auth */
    private String module;
    /** 动作过滤，如 tenant.create */
    private String action;
    /** 操作人 id */
    private Long operatorId;
    /** 目标对象类型 */
    private String targetType;
    /** 目标对象 id */
    private String targetId;
    /** 结果过滤：SUCCESS / FAIL */
    private String result;
    /** 起始时间（epoch millis） */
    private Long startAt;
    /** 结束时间（epoch millis） */
    private Long endAt;
}
