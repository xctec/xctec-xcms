package com.df4j.xctec.xcms.system.domain.converter;

import com.df4j.xctec.xcms.common.jpa.converter.ModelConverter;
import com.df4j.xctec.xcms.core.mapper.GlobalMapperConfig;
import com.df4j.xctec.xcms.system.domain.dto.OrgUnitDto;
import com.df4j.xctec.xcms.system.domain.entity.OrgUnit;
import com.df4j.xctec.xcms.system.domain.form.OrgUnitForm;
import org.mapstruct.Mapper;

@Mapper(config = GlobalMapperConfig.class)
public interface OrgUnitConverter extends ModelConverter<OrgUnit, OrgUnitDto, OrgUnitForm> {

}
