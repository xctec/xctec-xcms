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
     * 落库前钩子：对需要加密的密码做 BCrypt 加密。
     * 触发条件（避免"总是加密"破坏 F 修复——编辑空密码时需保留既有哈希）：
     *  - 新建记录（id 为 null）：表单里的密码一律视作明文，始终加密（提交 $2a$ 串也会被二次加密，杜绝哈希注入）；
     *  - 编辑/改密且表单显式提供了非空密码（passwordEncryptionForced=true）：加密之；
     *  - 编辑空密码：不触发，MapStruct IGNORE 已保留实体既有 BCrypt 哈希，原样落库。
     */
    @Override
    protected void onBeforePersist(User user) {
        boolean isNew = user.getId() == null;
        String password = user.getPassword();
        if ((isNew || user.isPasswordEncryptionForced())
                && password != null && !password.isBlank()) {
            user.setPassword(passwordEncoder.encode(password));
        }
    }

    /**
     * edit 前置：
     *  - 表单密码为空/空白 → 置为 null，使 MapStruct 的 IGNORE 策略（仅跳过 null 源）保留实体既有 BCrypt 哈希（F 修复）；
     *  - 表单提供了非空密码 → 标记需要加密，交由 onBeforePersist 统一加密（兼容编辑改密）。
     */
    @Override
    protected void beforeEdit(UserForm form, User entity) {
        if (form.getPassword() == null || form.getPassword().isBlank()) {
            form.setPassword(null);
        } else {
            entity.setPasswordEncryptionForced(true);
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
        user.setPasswordEncryptionForced(true);
        onBeforePersist(user);
        this.getRepository().save(user);
    }
}
