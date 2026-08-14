package com.df4j.xctec.xcms.system.domain.entity;

import com.df4j.xctec.xcms.common.jpa.entity.BaseAuditableEntity;
import com.df4j.xctec.xcms.common.jpa.entity.TenantScoped;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "sys_user", indexes = {
        @Index(name = "idx_sys_user_tenant_username", columnList = "tenant_id,username", unique = true)
})
public class User extends BaseAuditableEntity implements TenantScoped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", precision = 18, nullable = false, comment = "主键ID")
    private Long id;

    @Column(name = "tenant_id", precision = 18, nullable = false, comment = "租户ID")
    private Long tenantId;

    @Column(name = "username", length = 64, nullable = false, comment = "用户名/账号")
    private String username;

    @Column(name = "password", length = 100, nullable = false, comment = "密码")
    private String password;

    @Column(name = "nickname", length = 64, comment = "昵称")
    private String nickname;

    @Column(name = "email", length = 100, comment = "邮箱")
    private String email;

    @Column(name = "mobile", length = 20, comment = "手机号")
    private String mobile;

    @Column(name = "avatar", length = 255, comment = "头像地址")
    private String avatar;

    @Column(name = "enable_status", length = 20, comment = "启用状态")
    private String enableStatus;

    /**
     *  transient 标记：编辑/改密时若表单显式提供了密码，置为 true，由 onBeforePersist 统一加密。
     *  新建记录（id 为 null）天然需要加密；编辑空密码时不标记，从而保留既有哈希（F 修复）。
     *  非持久化字段。
     */
    @Transient
    private boolean passwordEncryptionForced;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "sys_user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private List<Role> roles;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "org_unit_id", comment = "所属机构ID")
    private OrgUnit orgUnit;
}
