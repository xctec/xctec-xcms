package com.df4j.xctec.xcms.system.domain.converter;

import com.df4j.xctec.xcms.common.jpa.converter.ModelConverter;
import com.df4j.xctec.xcms.core.mapper.GlobalMapperConfig;
import com.df4j.xctec.xcms.system.domain.dto.MenuDto;
import com.df4j.xctec.xcms.system.domain.entity.Menu;
import com.df4j.xctec.xcms.system.domain.form.MenuForm;
import org.mapstruct.Mapper;

@Mapper(config = GlobalMapperConfig.class)
public interface MenuConverter extends ModelConverter<Menu, MenuDto, MenuForm> {

}
