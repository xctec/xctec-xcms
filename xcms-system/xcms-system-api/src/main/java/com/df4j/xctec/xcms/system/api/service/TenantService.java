package com.df4j.xctec.xcms.system.api.service;

import com.df4j.xctec.xcms.core.result.PageVo;
import com.df4j.xctec.xcms.system.api.dto.TenantCreateDto;
import com.df4j.xctec.xcms.system.api.dto.TenantMoveDto;
import com.df4j.xctec.xcms.system.api.dto.TenantPageQuery;
import com.df4j.xctec.xcms.system.api.dto.TenantTreeVo;
import com.df4j.xctec.xcms.system.api.dto.TenantVo;

/**
 * 租户管理契约（接口）。实现位于 xcms-system-impl，遵循接口先行原则。
 */
public interface TenantService {

    /**
     * 创建租户（含子树位置）。调用方上下文租户必须为目标租户的管理祖先。
     *
     * @param dto 入参
     * @return 新建租户 id
     */
    Long create(TenantCreateDto dto);

    /**
     * 迁移租户子树（改父）。需校验目标父是管理祖先范围内，且不会形成环。
     *
     * @param dto 入参
     */
    void move(TenantMoveDto dto);

    /**
     * 分页查询租户（业务域 tenantId 精确隔离 + 管理域 path 下钻）。
     *
     * @param query 查询条件
     * @return 分页结果
     */
    PageVo<TenantVo> page(TenantPageQuery query);

    /**
     * 租户详情。
     *
     * @param id 租户 id
     * @return 详情
     */
    TenantVo detail(Long id);

    /**
     * 以当前上下文租户为根，返回整棵管理树。
     *
     * @return 树形结构
     */
    TenantTreeVo tree();
}
