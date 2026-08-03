package com.df4j.xctec.xcms.audit.api.service;

import com.df4j.xctec.xcms.audit.api.dto.AuditLogVo;
import com.df4j.xctec.xcms.audit.api.dto.AuditQueryDto;
import com.df4j.xctec.xcms.core.result.PageVo;

/**
 * 审计查询契约（接口）。落库由 xcms-audit-impl 监听 {@code AuditEvent} 异步完成，
 * 此处仅暴露只读查询能力，遵循接口先行原则。
 */
public interface AuditService {

    /**
     * 分页查询审计日志（业务域 tenantId 精确隔离）。
     *
     * @param query 查询条件
     * @return 分页结果
     */
    PageVo<AuditLogVo> page(AuditQueryDto query);

    /**
     * 审计记录详情。
     *
     * @param eventId 事件 id
     * @return 记录
     */
    AuditLogVo detail(String eventId);
}
