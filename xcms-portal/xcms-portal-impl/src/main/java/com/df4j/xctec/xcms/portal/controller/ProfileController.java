package com.df4j.xctec.xcms.portal.controller;

import com.df4j.xctec.xcms.common.security.authentication.userdetails.XcmsUserDetails;
import com.df4j.xctec.xcms.core.dto.LoginUserDto;
import com.df4j.xctec.xcms.core.utils.ResultUtils;
import com.df4j.xctec.xcms.core.vo.ResultVo;
import com.df4j.xctec.xcms.portal.api.ProfileApi;
import com.df4j.xctec.xcms.portal.api.vo.MenuVo;
import com.df4j.xctec.xcms.system.api.dto.UserMenuDto;
import com.df4j.xctec.xcms.system.api.query.UserMenuQuery;
import com.df4j.xctec.xcms.system.api.service.UserMenuService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 当前登录用户相关信息接口实现（portal-impl）。
 * <p>
 * 从 Spring Security 上下文取出认证时已还原的 {@link XcmsUserDetails} 主体，
 * 返回当前用户信息与菜单树。菜单数据来自 system-api 的 {@link UserMenuService} 契约。
 */
@RestController
public class ProfileController implements ProfileApi {

    private final UserMenuService userMenuService;

    public ProfileController(UserMenuService userMenuService) {
        this.userMenuService = userMenuService;
    }

    @Override
    public ResultVo<LoginUserDto> currentUser() {
        XcmsUserDetails userDetails = currentUserDetails();
        LoginUserDto dto = LoginUserDto.builder()
                .userId(userDetails.getUserId())
                .username(userDetails.getUsername())
                .nickname(userDetails.getNickname())
                .tenantId(userDetails.getTenantId())
                .avatar(userDetails.getAvatar())
                .mobile(userDetails.getMobile())
                .email(userDetails.getEmail())
                .encType(userDetails.getEncType())
                .roleCodes(userDetails.getRoleCodes())
                .enableStatus(userDetails.getEnableStatus())
                .build();
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
        List<MenuVo> voList = tree.stream().map(this::toMenuVo).collect(Collectors.toList());
        return ResultUtils.success(voList);
    }

    private MenuVo toMenuVo(UserMenuDto dto) {
        MenuVo vo = new MenuVo();
        vo.setId(dto.getId());
        vo.setParentId(dto.getParentId());
        vo.setMenuName(dto.getMenuName());
        vo.setMenuType(dto.getMenuType());
        vo.setRoutePath(dto.getRoutePath());
        vo.setComponent(dto.getComponent());
        vo.setIcon(dto.getIcon());
        vo.setPermission(dto.getPermission());
        vo.setVisibleStatus(dto.getVisibleStatus());
        vo.setEnableStatus(dto.getEnableStatus());
        if (dto.getChildren() != null) {
            vo.setChildren(dto.getChildren().stream().map(this::toMenuVo).collect(Collectors.toList()));
        }
        return vo;
    }

    private XcmsUserDetails currentUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof XcmsUserDetails)) {
            throw new UsernameNotFoundException("未登录或登录已失效");
        }
        return (XcmsUserDetails) authentication.getPrincipal();
    }
}
