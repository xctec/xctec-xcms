package com.df4j.xctec.xcms.system.domain.converter;

import com.df4j.xctec.xcms.common.jpa.converter.ModelConverter;
import com.df4j.xctec.xcms.core.mapper.GlobalMapperConfig;
import com.df4j.xctec.xcms.system.domain.dto.UserDto;
import com.df4j.xctec.xcms.system.domain.entity.User;
import com.df4j.xctec.xcms.system.domain.form.UserForm;
import org.mapstruct.Mapper;

@Mapper(config = GlobalMapperConfig.class)
public interface UserConverter extends ModelConverter<User, UserDto, UserForm> {

}
