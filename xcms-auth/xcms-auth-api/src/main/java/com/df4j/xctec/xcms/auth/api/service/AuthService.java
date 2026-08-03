package com.df4j.xctec.xcms.auth.api.service;

import com.df4j.xctec.xcms.auth.api.dto.KickDto;
import com.df4j.xctec.xcms.auth.api.dto.LoginDto;
import com.df4j.xctec.xcms.auth.api.dto.LoginVo;
import com.df4j.xctec.xcms.auth.api.dto.RefreshDto;

/**
 * 认证契约（接口）。实现位于 xcms-auth-impl，遵循接口先行原则。
 * 底层令牌存储复用 core 的 {@code TokenStore}，可替换为 Redis / JWT 实现而不改契约。
 */
public interface AuthService {

    /**
     * 登录：校验凭证 -> 组装 {@code AuthPrincipal} -> 签发 {@code TokenPair}。
     *
     * @param dto 登录入参
     * @return 令牌对与主体快照
     */
    LoginVo login(LoginDto dto);

    /**
     * 用 refreshToken 换取新的令牌对。
     *
     * @param dto 刷新入参
     * @return 新令牌对
     */
    LoginVo refresh(RefreshDto dto);

    /**
     * 登出：使当前 accessToken 失效。
     *
     * @param accessToken 当前访问令牌
     */
    void logout(String accessToken);

    /**
     * 踢线：按用户或按租户批量失效在线令牌（级联管理场景刚需）。
     *
     * @param dto 踢线入参
     */
    void kick(KickDto dto);

    /**
     * 校验访问令牌，返回主体（供过滤器链 / 权限 AOP 使用）。
     *
     * @param accessToken 访问令牌
     * @return 认证主体
     */
    com.df4j.xctec.xcms.core.security.AuthPrincipal verify(String accessToken);
}
