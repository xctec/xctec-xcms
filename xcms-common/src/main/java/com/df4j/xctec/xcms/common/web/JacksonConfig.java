package com.df4j.xctec.xcms.common.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.module.SimpleModule;
import tools.jackson.databind.ser.std.ToStringSerializer;

/**
 * Jackson 配置（适配 Jackson 3 / Spring Boot 4）：
 * - Long -> 字符串，避免前端精度丢失；
 * - JavaTime 支持由 Spring Boot 的 JacksonAutoConfiguration 默认注册，此处无需重复。
 */
@Configuration
public class JacksonConfig {

    @Bean
    @Primary
    public JsonMapper xcmsObjectMapper() {
        SimpleModule longModule = new SimpleModule();
        longModule.addSerializer(Long.class, ToStringSerializer.instance);
        longModule.addSerializer(Long.TYPE, ToStringSerializer.instance);

        return JsonMapper.builder()
                .addModule(longModule)
                .build();
    }
}
