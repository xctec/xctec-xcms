package com.df4j.xctec.xcms.system.domain.entity;

import com.df4j.xctec.xcms.common.jpa.entity.BaseTreeEntity;
import com.df4j.xctec.xcms.common.jpa.entity.TenantScoped;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "system_org_unit", indexes = {
        @Index(name = "idx_system_org_unit_tenant_code", columnList = "tenant_id,node_code", unique = true)
})
public class OrgUnit extends BaseTreeEntity implements TenantScoped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", precision = 18, nullable = false, comment = "主键ID")
    private Long id;

    @Column(name = "tenant_id", precision = 18, nullable = false, comment = "租户ID")
    private Long tenantId;

    @Column(name = "org_name", length = 100, nullable = false, comment = "机构名称")
    private String orgName;

    @Column(name = "enable_status", length = 20, comment = "启用状态")
    private String enableStatus;
}
