package com.df4j.xctec.xcms.system.service;

import com.df4j.xctec.xcms.common.jpa.service.BaseTreeService;
import com.df4j.xctec.xcms.system.domain.converter.MenuConverter;
import com.df4j.xctec.xcms.system.domain.dto.MenuDto;
import com.df4j.xctec.xcms.system.domain.entity.Menu;
import com.df4j.xctec.xcms.system.domain.entity.QMenu;
import com.df4j.xctec.xcms.system.domain.form.MenuForm;
import com.df4j.xctec.xcms.system.domain.query.MenuQuery;
import com.df4j.xctec.xcms.system.repository.MenuRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;
import lombok.Getter;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Getter
@Service
public class MenuService extends BaseTreeService<Menu, QMenu, MenuDto, MenuForm, MenuQuery, MenuConverter, MenuRepository> {

    private final QMenu q = QMenu.menu;

    private final NumberPath<Long> idPath = q.id;

    private final StringPath codePath = q.codePath;

    @Override
    public OrderSpecifier<?>[] getOrders(MenuQuery params) {
        return new OrderSpecifier[]{q.sortNum.asc()};
    }

    @Override
    public BooleanBuilder getWhere(MenuQuery params) {
        BooleanBuilder where = new BooleanBuilder();
        String keyword = params.getKeyword();
        if (StringUtils.hasText(keyword)) {
            where.andAnyOf(
                    q.menuName.contains(keyword),
                    q.permission.contains(keyword)
            );
        }
        if (StringUtils.hasText(params.getMenuName())) {
            where.and(q.menuName.contains(params.getMenuName()));
        }
        if (StringUtils.hasText(params.getMenuType())) {
            where.and(q.menuType.eq(params.getMenuType()));
        }
        if (StringUtils.hasText(params.getPermission())) {
            where.and(q.permission.contains(params.getPermission()));
        }
        if (StringUtils.hasText(params.getEnableStatus())) {
            where.and(q.enableStatus.eq(params.getEnableStatus()));
        }
        return where;
    }
}
