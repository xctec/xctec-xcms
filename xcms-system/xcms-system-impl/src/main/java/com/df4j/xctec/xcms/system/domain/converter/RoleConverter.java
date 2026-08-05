package com.df4j.xctec.xcms.system.domain.converter;

import com.df4j.xctec.xcms.common.jpa.converter.ModelConverter;
import com.df4j.xctec.xcms.core.mapper.GlobalMapperConfig;
import com.df4j.xctec.xcms.system.domain.dto.RoleDto;
import com.df4j.xctec.xcms.system.domain.entity.Role;
import com.df4j.xctec.xcms.system.domain.form.RoleForm;
import org.mapstruct.Mapper;

@Mapper(config = GlobalMapperConfig.class)
public interface RoleConverter extends ModelConverter<Role, RoleDto, RoleForm> {

}
