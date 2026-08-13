package com.df4j.xctec.xcms.portal.api;

import com.df4j.xctec.xcms.core.dto.LoginUserDto;
import com.df4j.xctec.xcms.core.vo.ResultVo;
import com.df4j.xctec.xcms.portal.api.vo.MenuVo;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * 当前登录用户相关信息接口契约（定义在 portal-api，实现在 portal-impl）。
 */
public interface ProfileApi {

    @GetMapping("/portal/current-user")
    ResultVo<LoginUserDto> currentUser();

    @GetMapping("/portal/current-menus")
    ResultVo<List<MenuVo>> currentMenus();
}
