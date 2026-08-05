package com.df4j.xctec.xcms.system.domain.entity;

import com.df4j.xctec.xcms.common.jpa.entity.BaseTreeEntity;
import com.df4j.xctec.xcms.common.jpa.entity.TenantScoped;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "sys_menu", indexes = {
        @Index(name = "idx_sys_menu_tenant_code", columnList = "tenant_id,node_code", unique = true)
})
public class Menu extends BaseTreeEntity implements TenantScoped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", precision = 18, nullable = false, comment = "主键ID")
    private Long id;

    @Column(name = "tenant_id", precision = 18, nullable = false, comment = "租户ID")
    private Long tenantId;

    @Column(name = "menu_name", length = 100, nullable = false, comment = "菜单名称")
    private String menuName;

    @Column(name = "menu_type", length = 20, nullable = false, comment = "菜单类型: 目录/菜单/按钮")
    private String menuType;

    @Column(name = "route_path", length = 200, comment = "路由路径")
    private String routePath;

    @Column(name = "component", length = 200, comment = "前端组件路径")
    private String component;

    @Column(name = "icon", length = 100, comment = "图标")
    private String icon;

    @Column(name = "permission", length = 100, comment = "权限标识")
    private String permission;

    @Column(name = "visible_status", length = 20, comment = "是否可见")
    private String visibleStatus;

    @Column(name = "enable_status", length = 20, comment = "启用状态")
    private String enableStatus;
}
