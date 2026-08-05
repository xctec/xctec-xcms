package com.df4j.xctec.xcms.system.service;

import com.df4j.xctec.xcms.common.jpa.service.BaseTreeService;
import com.df4j.xctec.xcms.system.domain.converter.TenantInfoConverter;
import com.df4j.xctec.xcms.system.domain.dto.TenantInfoDto;
import com.df4j.xctec.xcms.system.domain.entity.QTenantInfo;
import com.df4j.xctec.xcms.system.domain.entity.TenantInfo;
import com.df4j.xctec.xcms.system.domain.form.TenantInfoForm;
import com.df4j.xctec.xcms.system.domain.query.TenantInfoQuery;
import com.df4j.xctec.xcms.system.repository.TenantInfoRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;
import lombok.Getter;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Getter
@Service
public class TenantInfoService
        extends BaseTreeService<TenantInfo, QTenantInfo, TenantInfoDto, TenantInfoForm,
        TenantInfoQuery, TenantInfoConverter, TenantInfoRepository> {

    private final QTenantInfo q = QTenantInfo.tenantInfo;

    private final NumberPath<Long> idPath = q.id;

    private final StringPath codePath = q.codePath;

    @Override
    public OrderSpecifier<?>[] getOrders(TenantInfoQuery params) {
        return super.getOrders(params);
    }

    @Override
    public BooleanBuilder getWhere(TenantInfoQuery params) {
        BooleanBuilder where = new BooleanBuilder();
        String keyword = params.getKeyword();
        if (StringUtils.hasText(keyword)) {
            where.andAnyOf(
                    q.nodeCode.contains(keyword),
                    q.tenantName.contains(keyword)
            );
        }
        return where;
    }
}
