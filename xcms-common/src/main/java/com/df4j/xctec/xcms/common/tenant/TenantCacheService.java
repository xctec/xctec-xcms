package com.df4j.xctec.xcms.common.tenant;

import java.util.List;
import java.util.Optional;

/**
 * 租户缓存读取抽象。由 system-impl 提供实现（基于 sys_tenant 表 + MemoryCache）。
 * common 层只依赖此接口，避免反向依赖业务模块。
 */
public interface TenantCacheService {

    Optional<TenantInfo> getTenant(Long tenantId);

    /** 返回 tenantId 的所有祖先（含自身） */
    List<Long> ancestorIds(Long tenantId);

    /** 判断 ancestor 是否为 descendant 的祖先（或相等） */
    boolean isAncestor(Long ancestor, Long descendant);
}
