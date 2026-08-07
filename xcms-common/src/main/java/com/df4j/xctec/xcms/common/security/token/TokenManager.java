package com.df4j.xctec.xcms.common.security.token;

import com.df4j.xctec.xcms.core.vo.TokenVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;


public interface TokenManager {


    Logger logger = LoggerFactory.getLogger(TokenManager.class);

    /**
     * 登录接口中，将spring security的登录认证信息转换为token，只有登录成功才需要转
     *
     * @param authentication 认证信息
     * @return TokenVo
     */
    TokenVo generate(Authentication authentication);

    /**
     * 校验token
     *
     * @param token token
     * @return 结果
     */
    boolean validate(String token);

    /**
     * 将token字符串转换为SpringSecurity的Authentication
     *
     * @param token token字符串
     * @return SpringSecurity Authentication对象
     */
    Authentication parse(String token);

    /**
     * 校验refreshToken
     *
     * @param refreshToken 刷新token
     * @return 结果
     */
    boolean validateRefreshToken(String refreshToken);

    /**
     * 使用refreshToken生成新的token
     *
     * @param refreshToken 刷新token
     * @return TokenVo
     */
    TokenVo refreshToken(String refreshToken);

    /**
     * 清理token
     *
     * @param token token
     */
    void invalid(String token);

    /**
     * 踢掉指定会话
     *
     * @param token token
     */
    default void kickSession(String token) {
        logger.warn("kickSession未实现!, token: {}", token);
    }

    /**
     * 踢掉指定用户的所有会话
     *
     * @param userId 用户ID
     */
    default void kickUserSessions(Long userId) {
        logger.warn("kickUserSessions未实现!, userId: {}", userId);
    }
}
