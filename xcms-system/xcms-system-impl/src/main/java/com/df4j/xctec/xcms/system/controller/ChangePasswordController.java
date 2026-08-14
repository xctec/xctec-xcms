package com.df4j.xctec.xcms.system.controller;

import com.df4j.xctec.xcms.common.security.authentication.userdetails.XcmsUserDetails;
import com.df4j.xctec.xcms.core.exception.BizException;
import com.df4j.xctec.xcms.core.utils.ResultUtils;
import com.df4j.xctec.xcms.core.vo.ResultVo;
import com.df4j.xctec.xcms.system.api.ChangePasswordApi;
import com.df4j.xctec.xcms.system.api.dto.ChangePasswordRequest;
import com.df4j.xctec.xcms.system.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;

/**
 * 修改密码接口实现（system-impl）。
 * <p>
 * 当前登录用户从 SecurityContext 的 principal（{@link XcmsUserDetails}）取得，
 * 并交由 {@link UserService#changePassword} 按当前租户取数、校验旧密码后写入新密码。
 */
@RestController
@RequiredArgsConstructor
public class ChangePasswordController implements ChangePasswordApi {

    private final UserService userService;

    @Override
    public ResultVo<Void> changePassword(ChangePasswordRequest request) {
        Long userId = currentUserId();
        userService.changePassword(userId, request.getOldPassword(), request.getNewPassword());
        return ResultUtils.success();
    }

    private Long currentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof XcmsUserDetails userDetails)) {
            throw BizException.of("401", "未认证，无法修改密码");
        }
        return userDetails.getUserId();
    }
}
