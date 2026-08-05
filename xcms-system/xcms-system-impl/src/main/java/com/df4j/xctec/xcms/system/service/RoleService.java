package com.df4j.xctec.xcms.system.service;

import com.df4j.xctec.xcms.common.jpa.service.BaseService;
import com.df4j.xctec.xcms.system.domain.converter.RoleConverter;
import com.df4j.xctec.xcms.system.domain.dto.RoleDto;
import com.df4j.xctec.xcms.system.domain.entity.QRole;
import com.df4j.xctec.xcms.system.domain.entity.Role;
import com.df4j.xctec.xcms.system.domain.form.RoleForm;
import com.df4j.xctec.xcms.system.domain.query.RoleQuery;
import com.df4j.xctec.xcms.system.repository.RoleRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.NumberPath;
import lombok.Getter;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Getter
@Service
public class RoleService
        extends BaseService<Role, QRole, RoleDto, RoleForm,
        RoleQuery, RoleConverter, RoleRepository> {

    private final QRole q = QRole.role;

    private final NumberPath<Long> idPath = q.id;

    @Override
    public OrderSpecifier<?>[] getOrders(RoleQuery params) {
        return super.getOrders(params);
    }

    @Override
    public BooleanBuilder getWhere(RoleQuery params) {
        BooleanBuilder where = new BooleanBuilder();
        String keyword = params.getKeyword();
        if (StringUtils.hasText(keyword)) {
            where.andAnyOf(
                    q.roleCode.contains(keyword),
                    q.roleName.contains(keyword)
            );
        }
        if (StringUtils.hasText(params.getRoleCode())) {
            where.and(q.roleCode.contains(params.getRoleCode()));
        }
        if (StringUtils.hasText(params.getRoleName())) {
            where.and(q.roleName.contains(params.getRoleName()));
        }
        return where;
    }
}
