package com.df4j.xctec.xcms.core.context;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
public class SecurityContext {

    private TenantInfo tenantInfo;
    private LoginUser loginUser;

    /**
     * 提权标志。默认 {@code false}：数据作用域过滤层必须按 {@code tenantInfo} 强制过滤。
     * 置为 {@code true} 时，表示本次请求临时放宽数据作用域（越过默认租户边界），
     * 具体放行范围由 {@link #elevatedScope} 决定。
     */
    @Builder.Default
    private boolean elevated = false;

    /**
     * 提权作用域。仅当 {@link #elevated} 为 {@code true} 时有效，
     * 描述本次提权允许访问的目标与约束。
     */
    private ElevatedScope elevatedScope;

    @Data
    @Builder
    public static class LoginUser {
        private Long userId;
        private String username;
        private String nickname;
        private Set<String> roleCodes;
    }

    @Data
    @Builder
    public static class TenantInfo {
        private Long tenantId;
        private String tenantCode;
        private String tenantName;
    }

    /**
     * 提权作用域：描述一次提权的目标与约束。
     *
     * <p>当前主用法是“提权到单个目标租户”（{@link #targetTenantId}）；其余字段为预留扩展位，
     * 供未来支持一次提权跨多租户、携带提权原因/审计人、设置有效期等，避免频繁改动上下文结构。
     */
    @Data
    @Builder
    public static class ElevatedScope {
        /**
         * 提权目标租户 ID（当前主要用法：提权到单个租户）
         */
        private Long targetTenantId;

        /**
         * 预留：提权目标租户集合，未来支持一次提权跨多个租户
         */
        private Set<Long> targetTenantIds;

        /**
         * 预留：提权原因/备注，供审计追溯
         */
        private String reason;

        /**
         * 预留：发起提权的操作人 ID，供审计追溯
         */
        private Long elevatedBy;

        /**
         * 预留：提权生效截止时间，供超时自动失效
         */
        private LocalDateTime expiresAt;

        public static ElevatedScope ofTenant(Long tenantId) {
            return ElevatedScope.builder().targetTenantId(tenantId).build();
        }
    }

    /**
     * 仅租户上下文（如登录页、门户、租户级定时任务等无登录用户的场景）。
     */
    public static SecurityContext ofTenantOnly(TenantInfo tenantInfo) {
        return SecurityContext.builder()
                .tenantInfo(tenantInfo)
                .build();
    }

    /**
     * 租户 + 登录用户上下文（系统最常见的场景）。
     */
    public static SecurityContext ofUser(TenantInfo tenantInfo, LoginUser loginUser) {
        return SecurityContext.builder()
                .tenantInfo(tenantInfo)
                .loginUser(loginUser)
                .build();
    }
}
