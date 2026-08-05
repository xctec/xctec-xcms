package com.df4j.xctec.xcms.system.domain.entity;

import com.df4j.xctec.xcms.common.jpa.entity.BaseAuditableEntity;
import com.df4j.xctec.xcms.common.jpa.entity.TenantScoped;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(
        name = "sys_role",
        indexes = {
                @Index(name = "idx_sys_role_tenant_code", columnList = "tenant_id,role_code", unique = true)
        },
        comment = "角色表"
)
public class Role extends BaseAuditableEntity implements TenantScoped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", precision = 18, nullable = false, comment = "主键ID")
    private Long id;

    @Column(name = "tenant_id", precision = 18, nullable = false, comment = "租户ID")
    private Long tenantId;

    @Column(name = "role_code", nullable = false, length = 64, comment = "角色代码")
    private String roleCode;

    @Column(name = "role_name", nullable = false, length = 64, comment = "角色名称")
    private String roleName;

    @Column(name = "role_desc", length = 255, comment = "角色说明")
    private String roleDesc;

    @Column(name = "enable_status", length = 20)
    private String enableStatus;
}
