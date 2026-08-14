package com.df4j.xctec.xcms.system.api.service;

import com.df4j.xctec.xcms.system.api.dto.UserMenuDto;
import com.df4j.xctec.xcms.system.api.query.UserMenuQuery;

import java.util.List;

/**
 * 用户菜单服务端口接口，由 system-impl 提供实现。
 * <p>
 * 与领域 CRUD 服务 {@code com.df4j.xctec.xcms.system.service.MenuService} 解耦：
 * 消费方（如 portal）仅依赖本接口与 {@code system-api} 的 DTO/Query，
 * 不依赖 system 的实现层，也不接触增删改能力。
 */
public interface UserMenuService {

    List<UserMenuDto> listByUser(UserMenuQuery query);
}
