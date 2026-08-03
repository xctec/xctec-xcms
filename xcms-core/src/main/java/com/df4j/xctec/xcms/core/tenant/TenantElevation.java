package com.df4j.xctec.xcms.core.tenant;

import java.util.function.Supplier;

/**
 * 受控提权：访问受限租户时显式提升上下文并必留审计。
 * 不对前端暴露「切换租户视角」接口。跨租户操作只能由服务端在明确业务语义下发起。
 * 默认实现在 xcms-base（DefaultTenantElevation），可被业务模块覆盖。
 */
public interface TenantElevation {

    /**
     * 提权到目标租户执行操作，执行完毕自动还原上下文。
     *
     * @param targetTenantId 目标租户 id（必须通过 TenantGuard.assertManageable）
     * @param action         在提权上下文中执行的操作
     * @return 操作返回值
     */
    <T> T runAsTenant(Long targetTenantId, Supplier<T> action);
}
