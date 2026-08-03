package com.df4j.xctec.xcms.web;

import org.mapstruct.MapperConfig;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;

/**
 * 全局 MapStruct 映射配置。
 *
 * - unmappedTargetPolicy = ERROR：强制显式映射，字段增删编译期报错。
 * - nullValueCheckStrategy = ALWAYS：源属性为 null 时不覆盖目标。
 *
 * 各 impl 的 Mapper 接口标注 @Mapper(config = XcmsMapperConfig.class) 即继承此配置。
 */
@MapperConfig(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface XcmsMapperConfig {
}
