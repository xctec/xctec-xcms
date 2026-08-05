package com.df4j.xctec.xcms.system.service;

import com.df4j.xctec.xcms.common.jpa.service.BaseTreeService;
import com.df4j.xctec.xcms.system.domain.converter.OrgUnitConverter;
import com.df4j.xctec.xcms.system.domain.dto.OrgUnitDto;
import com.df4j.xctec.xcms.system.domain.entity.OrgUnit;
import com.df4j.xctec.xcms.system.domain.entity.QOrgUnit;
import com.df4j.xctec.xcms.system.domain.form.OrgUnitForm;
import com.df4j.xctec.xcms.system.domain.query.OrgUnitQuery;
import com.df4j.xctec.xcms.system.repository.OrgUnitRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;
import lombok.Getter;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Getter
@Service
public class OrgUnitService extends BaseTreeService<OrgUnit, QOrgUnit, OrgUnitDto, OrgUnitForm, OrgUnitQuery, OrgUnitConverter, OrgUnitRepository> {

    private final QOrgUnit q = QOrgUnit.orgUnit;

    private final NumberPath<Long> idPath = q.id;

    private final StringPath codePath = q.codePath;

    @Override
    public OrderSpecifier<?>[] getOrders(OrgUnitQuery params) {
        return new OrderSpecifier[]{q.sortNum.asc()};
    }

    @Override
    public BooleanBuilder getWhere(OrgUnitQuery params) {
        BooleanBuilder where = new BooleanBuilder();
        String keyword = params.getKeyword();
        if (StringUtils.hasText(keyword)) {
            where.and(q.orgName.contains(keyword));
        }
        if (StringUtils.hasText(params.getOrgName())) {
            where.and(q.orgName.contains(params.getOrgName()));
        }
        if (StringUtils.hasText(params.getEnableStatus())) {
            where.and(q.enableStatus.eq(params.getEnableStatus()));
        }
        return where;
    }
}
