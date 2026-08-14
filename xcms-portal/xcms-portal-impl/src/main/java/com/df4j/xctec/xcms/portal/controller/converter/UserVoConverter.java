package com.df4j.xctec.xcms.portal.controller.converter;

import com.df4j.xctec.xcms.common.security.authentication.userdetails.XcmsUserDetails;
import com.df4j.xctec.xcms.core.dto.LoginUserDto;
import com.df4j.xctec.xcms.core.mapper.GlobalMapperConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

/**
 * 认证主体 {@link XcmsUserDetails} 到前端用户视图 {@link LoginUserDto} 的转换。
 * <p>
 * 字段名在两者间一一对应，由 MapStruct 自动映射。
 * 忽略 {@code password}（避免将编码后的凭证回传前端）；
 * 其余来自 {@code UserDetails} 接口的派生属性（authorities / 账户状态标志）无对应目标字段，
 * 通过 {@link ReportingPolicy#IGNORE} 抑制未映射源属性告警。
 */
@Mapper(config = GlobalMapperConfig.class, unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface UserVoConverter {

    @Mapping(target = "password", ignore = true)
    LoginUserDto toLoginUserDto(XcmsUserDetails details);
}
