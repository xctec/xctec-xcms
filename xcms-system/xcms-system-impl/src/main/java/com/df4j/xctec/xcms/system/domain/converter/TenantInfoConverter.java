package com.df4j.xctec.xcms.system.domain.converter;

import com.df4j.xctec.xcms.common.jpa.converter.ModelConverter;
import com.df4j.xctec.xcms.core.mapper.GlobalMapperConfig;
import com.df4j.xctec.xcms.system.domain.dto.TenantInfoDto;
import com.df4j.xctec.xcms.system.domain.entity.TenantInfo;
import com.df4j.xctec.xcms.system.domain.form.TenantInfoForm;
import org.mapstruct.Mapper;

@Mapper(config = GlobalMapperConfig.class)
public interface TenantInfoConverter extends ModelConverter<TenantInfo, TenantInfoDto, TenantInfoForm> {

}
