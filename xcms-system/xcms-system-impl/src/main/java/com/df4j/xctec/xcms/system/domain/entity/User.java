package com.df4j.xctec.xcms.system.domain.entity;

import com.df4j.xctec.xcms.common.jpa.entity.BaseAuditableEntity;
import com.df4j.xctec.xcms.common.jpa.entity.TenantScoped;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @Column(name = "org_unit_id", precision = 18, comment = "所属机构ID")
    private Long orgUnitId;

    @Column(name = "enable_status", length = 20, comment = "启用状态")
    private String enableStatus;
}
