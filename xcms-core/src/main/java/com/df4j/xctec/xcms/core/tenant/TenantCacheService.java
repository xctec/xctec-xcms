package com.df4j.xctec.xcms.core.tenant;

import java.util.List;
import java.util.Optional;

/**
 * 租户缓存读取抽象。由 system-impl 提供实现（基于 sys_tenant 表 + MemoryCache）。
 * core 层只依赖此接口，避免反向依赖业务模块。
 */
public interface TenantCacheService {

    Optional<TenantInfo> get(Long tenantId);

    /**
     * 返回 tenantId 的所有祖先 id（不含自身，含根 0）。
     */
    List<Long> ancestorIds(Long tenantId);

    /**
     * 判断 ancestor 是否为 descendant 的祖先（不含相等）。
     * 即 descendant 的 path 是否以 ancestor 的 scopePrefix() 开头。
     */
    boolean isAncestor(Long ancestor, Long descendant);
}
