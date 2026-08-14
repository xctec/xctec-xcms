package com.df4j.xctec.xcms.portal.controller;

import com.df4j.xctec.xcms.common.security.authentication.userdetails.XcmsUserDetails;
import com.df4j.xctec.xcms.core.dto.LoginUserDto;
import com.df4j.xctec.xcms.core.exception.BizException;
import com.df4j.xctec.xcms.core.utils.ResultUtils;
import com.df4j.xctec.xcms.core.vo.ResultVo;
import com.df4j.xctec.xcms.portal.api.ProfileApi;
import com.df4j.xctec.xcms.portal.api.vo.MenuVo;
import com.df4j.xctec.xcms.portal.controller.converter.MenuVoConverter;
import com.df4j.xctec.xcms.portal.controller.converter.UserVoConverter;
import com.df4j.xctec.xcms.system.api.dto.UserMenuDto;
import com.df4j.xctec.xcms.system.api.query.UserMenuQuery;
import com.df4j.xctec.xcms.system.api.service.UserMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 当前登录用户相关信息接口实现（portal-impl）。
 * <p>
 * 从 Spring Security 上下文取出认证时已还原的 {@link XcmsUserDetails} 主体，
 * 返回当前用户信息与菜单树。菜单数据来自 system-api 的 {@link UserMenuService} 契约。
 */
@RestController
public class ProfileController implements ProfileApi {

    private final UserMenuService userMenuService;
    private final MenuVoConverter menuVoConverter;
    private final UserVoConverter userVoConverter;

    @Autowired
    public ProfileController(UserMenuService userMenuService,
                             MenuVoConverter menuVoConverter,
                             UserVoConverter userVoConverter) {
        this.userMenuService = userMenuService;
        this.menuVoConverter = menuVoConverter;
        this.userVoConverter = userVoConverter;
    }

    @Override
    public ResultVo<LoginUserDto> currentUser() {
        XcmsUserDetails userDetails = currentUserDetails();
        LoginUserDto dto = userVoConverter.toLoginUserDto(userDetails);
        return ResultUtils.success(dto);
    }

    @Override
    public ResultVo<List<MenuVo>> currentMenus() {
        XcmsUserDetails userDetails = currentUserDetails();
        UserMenuQuery query = new UserMenuQuery();
        query.setTenantId(userDetails.getTenantId());
        query.setUserId(userDetails.getUserId());
        query.setRoleCodes(userDetails.getRoleCodes());
        List<UserMenuDto> tree = userMenuService.listByUser(query);
        List<MenuVo> voList = menuVoConverter.toVoList(tree);
        return ResultUtils.success(voList);
    }

    private XcmsUserDetails currentUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof XcmsUserDetails)) {
            throw new BizException("401", "未登录或登录已失效");
        }
        return (XcmsUserDetails) authentication.getPrincipal();
    }
}
