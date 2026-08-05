package com.df4j.xctec.xcms.system.domain.entity;

import com.df4j.xctec.xcms.common.jpa.entity.BaseTreeEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(
        name = "system_tenant_info",
        indexes = {
                @Index(name = "idx_system_tenant_code", columnList = "node_code", unique = true)
        },
        comment = "租户信息表"
)
public class TenantInfo extends BaseTreeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", precision = 18, nullable = false, comment = "主键ID")
    private Long id;

    @Column(name = "tenant_name", length = 100, nullable = false, comment = "租户名称")
    private String tenantName;

    @Column(name = "enable_status", length = 1, nullable = false, comment = "租户状态")
    private Integer enableStatus;

}
