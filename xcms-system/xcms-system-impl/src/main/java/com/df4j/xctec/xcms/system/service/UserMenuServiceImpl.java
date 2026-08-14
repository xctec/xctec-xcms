package com.df4j.xctec.xcms.system.service;

import com.df4j.xctec.xcms.system.api.dto.UserMenuDto;
import com.df4j.xctec.xcms.system.api.query.UserMenuQuery;
import com.df4j.xctec.xcms.system.api.service.UserMenuService;
import com.df4j.xctec.xcms.system.domain.entity.Menu;
import com.df4j.xctec.xcms.system.domain.entity.QMenu;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 用户菜单端口实现。
 * <p>
 * 过滤策略（方案 A，契约先行 + 稳妥默认）：
 * <ol>
 *   <li>按 {@code tenantId} 隔离，仅返回启用的菜单；</li>
 *   <li>权限过滤：无 permission 标识的菜单视为公开，否则要求 permission 命中 roleCodes；
 *       roleCodes 为空（RBAC 关联尚未接入）时，仅返回公开菜单。</li>
 * </ol>
 * 后续建立 user→role→menu 关联后，roleCodes 将由关联数据填充，本实现无需改动。
 */
@Service
@RequiredArgsConstructor
public class UserMenuServiceImpl implements UserMenuService {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<UserMenuDto> listByUser(UserMenuQuery query) {
        QMenu q = QMenu.menu;
        BooleanBuilder where = new BooleanBuilder();
        if (query.getTenantId() != null) {
            where.and(q.tenantId.eq(query.getTenantId()));
        }
        if (StringUtils.hasText(query.getMenuType())) {
            where.and(q.menuType.eq(query.getMenuType()));
        }
        if (StringUtils.hasText(query.getEnableStatus())) {
            where.and(q.enableStatus.eq(query.getEnableStatus()));
        }
        if (StringUtils.hasText(query.getVisibleStatus())) {
            where.and(q.visibleStatus.eq(query.getVisibleStatus()));
        }

        List<Menu> menus = jpaQueryFactory.selectFrom(q)
                .where(where)
                .orderBy(q.sortNum.desc())
                .fetch();
        Set<String> roleCodes = query.getRoleCodes();
        List<UserMenuDto> dtos = menus.stream()
                .filter(m -> isVisibleToRole(m, roleCodes))
                .map(this::toDto)
                .collect(Collectors.toList());
        return buildTree(dtos);
    }

    private boolean isVisibleToRole(Menu menu, Set<String> roleCodes) {
        String permission = menu.getPermission();
        if (!StringUtils.hasText(permission)) {
            return true;
        }
        return roleCodes != null && roleCodes.contains(permission);
    }

    private UserMenuDto toDto(Menu menu) {
        UserMenuDto dto = new UserMenuDto();
        dto.setId(menu.getId());
        dto.setParentId(menu.getParentId());
        dto.setMenuName(menu.getMenuName());
        dto.setMenuType(menu.getMenuType());
        dto.setRoutePath(menu.getRoutePath());
        dto.setComponent(menu.getComponent());
        dto.setIcon(menu.getIcon());
        dto.setPermission(menu.getPermission());
        dto.setVisibleStatus(menu.getVisibleStatus());
        dto.setEnableStatus(menu.getEnableStatus());
        dto.setSortNum(menu.getSortNum());
        return dto;
    }

    private List<UserMenuDto> buildTree(List<UserMenuDto> list) {
        List<Long> ids = list.stream().map(UserMenuDto::getId).collect(Collectors.toList());
        return list.stream()
                .filter(x -> !ids.contains(x.getParentId()))
                .peek(x -> x.setChildren(buildSubTree(x.getId(), list)))
                .collect(Collectors.toList());
    }

    private List<UserMenuDto> buildSubTree(Long id, List<UserMenuDto> list) {
        return list.stream()
                .filter(x -> id.equals(x.getParentId()))
                .peek(x -> x.setChildren(buildSubTree(x.getId(), list)))
                .collect(Collectors.toList());
    }
}
