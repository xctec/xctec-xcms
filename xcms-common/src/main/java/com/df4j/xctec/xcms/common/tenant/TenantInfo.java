package com.df4j.xctec.xcms.common.tenant;

import java.io.Serializable;
import java.util.Set;

/**
 * 租户信息（管理域）。注意：Tenant 实体不带 @TenantId，否则摧毁级联下钻。
 * 此 DTO 用于上下文与缓存，不含 JPA 注解。
 */
public class TenantInfo implements Serializable {

    private Long id;
    private Long parentId;
    /** 祖先 id 路径（不含自身） */
    private String path;
    /** 含自身的 code 路径 */
    private String codePath;
    private Integer level;
    private Set<Long> ancestorIds;

    public TenantInfo() {
    }

    public TenantInfo(Long id, Long parentId, String path, String codePath, Integer level) {
        this.id = id;
        this.parentId = parentId;
        this.path = path;
        this.codePath = codePath;
        this.level = level;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getCodePath() {
        return codePath;
    }

    public void setCodePath(String codePath) {
        this.codePath = codePath;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Set<Long> getAncestorIds() {
        return ancestorIds;
    }

    public void setAncestorIds(Set<Long> ancestorIds) {
        this.ancestorIds = ancestorIds;
    }
}
