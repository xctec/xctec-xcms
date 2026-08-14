package com.df4j.xctec.xcms.system.service;

import com.df4j.xctec.xcms.common.jpa.service.BaseService;
import com.df4j.xctec.xcms.system.domain.converter.UserConverter;
import com.df4j.xctec.xcms.system.domain.dto.UserDto;
import com.df4j.xctec.xcms.system.domain.entity.QUser;
import com.df4j.xctec.xcms.system.domain.entity.User;
import com.df4j.xctec.xcms.system.domain.form.UserForm;
import com.df4j.xctec.xcms.system.domain.query.UserQuery;
import com.df4j.xctec.xcms.system.repository.UserRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.NumberPath;
import lombok.Getter;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Getter
@Service
public class UserService extends BaseService<User, QUser, UserDto, UserForm, UserQuery, UserConverter, UserRepository> {

    private final QUser q = QUser.user;

    private final NumberPath<Long> idPath = q.id;

    @Override
    public OrderSpecifier<?>[] getOrders(UserQuery params) {
        return new OrderSpecifier[]{q.id.desc()};
    }

    @Override
    public BooleanBuilder getWhere(UserQuery params) {
        BooleanBuilder where = new BooleanBuilder();
        String keyword = params.getKeyword();
        if (StringUtils.hasText(keyword)) {
            where.andAnyOf(
                    q.username.contains(keyword),
                    q.nickname.contains(keyword),
                    q.mobile.contains(keyword)
            );
        }
        if (StringUtils.hasText(params.getUsername())) {
            where.and(q.username.contains(params.getUsername()));
        }
        if (StringUtils.hasText(params.getNickname())) {
            where.and(q.nickname.contains(params.getNickname()));
        }
        if (StringUtils.hasText(params.getMobile())) {
            where.and(q.mobile.contains(params.getMobile()));
        }
        if (StringUtils.hasText(params.getEnableStatus())) {
            where.and(q.enableStatus.eq(params.getEnableStatus()));
        }
        return where;
    }
}
