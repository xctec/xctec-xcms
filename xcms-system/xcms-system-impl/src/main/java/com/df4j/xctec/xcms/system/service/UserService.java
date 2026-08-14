package com.df4j.xctec.xcms.system.service;

import com.df4j.xctec.xcms.common.jpa.service.BaseService;
import com.df4j.xctec.xcms.core.exception.BizException;
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
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Getter
@Service
public class UserService extends BaseService<User, QUser, UserDto, UserForm, UserQuery, UserConverter, UserRepository> {

    private final QUser q = QUser.user;

    private final NumberPath<Long> idPath = q.id;

    @Setter(onMethod_ = {@Autowired})
    private PasswordEncoder passwordEncoder;

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

    /**
     * 落库前钩子：对明文密码做 BCrypt 加密。
     * 规则：密码为空保留原值；已为 BCrypt 哈希（以 $2a$/$2b$ 开头）不重复加密；其余视为明文加密。
     */
    @Override
    protected void onBeforePersist(User user) {
        String password = user.getPassword();
        if (password == null || password.isBlank()) {
            return;
        }
        if (password.startsWith("$2a$") || password.startsWith("$2b$")) {
            return;
        }
        user.setPassword(passwordEncoder.encode(password));
    }

    /**
     * edit 前置：表单密码为 null/空白时置为 null，使 MapStruct 的 IGNORE 策略
     * 保留实体既有 BCrypt 哈希，避免把已加密密码清空为 ""（高危）。
     */
    @Override
    protected void beforeEdit(UserForm form, User entity) {
        if (form.getPassword() == null || form.getPassword().isBlank()) {
            form.setPassword(null);
        }
    }

    /**
     * 修改密码：按当前租户取数，校验旧密码后写入新密码（BCrypt 加密）。
     *
     * @param userId       当前登录用户ID（来自 principal）
     * @param oldPassword  旧密码明文
     * @param newPassword  新密码明文
     */
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        if (newPassword == null || newPassword.isBlank()) {
            throw BizException.of("400", "新密码不能为空");
        }
        User user = this.getRepository()
                .findById(userId)
                .orElseThrow(() -> BizException.of("-1", "用户不存在"));
        // 租户隔离：仅允许修改本租户下的用户
        ensureSameTenant(user);
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw BizException.of("400", "旧密码不正确");
        }
        user.setPassword(newPassword);
        onBeforePersist(user);
        this.getRepository().save(user);
    }
}
