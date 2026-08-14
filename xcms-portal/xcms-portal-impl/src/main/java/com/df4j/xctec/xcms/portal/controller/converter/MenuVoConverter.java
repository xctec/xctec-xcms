package com.df4j.xctec.xcms.portal.controller.converter;

import com.df4j.xctec.xcms.core.mapper.GlobalMapperConfig;
import com.df4j.xctec.xcms.portal.api.vo.MenuVo;
import com.df4j.xctec.xcms.system.api.dto.UserMenuDto;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * 用户菜单只读 DTO 到前端视图对象 {@link MenuVo} 的转换。
 * <p>
 * 字段名在 {@code UserMenuDto} 与 {@code MenuVo} 间一一对应，由 MapStruct 自动映射；
 * {@code children} 递归映射，无需额外配置。
 */
@Mapper(config = GlobalMapperConfig.class)
public interface MenuVoConverter {

    MenuVo toVo(UserMenuDto dto);

    List<MenuVo> toVoList(List<UserMenuDto> dtoList);
}
