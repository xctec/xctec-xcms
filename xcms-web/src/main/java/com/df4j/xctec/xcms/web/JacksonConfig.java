package com.df4j.xctec.xcms.web;

import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.module.SimpleModule;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Jackson 全局配置（Jackson 3.x / Spring Boot 4.x）。
 *
 * - Long / BigInteger / BigDecimal 序列化为字符串，规避 JS 精度丢失。
 * - LocalDateTime 格式化为 yyyy-MM-dd HH:mm:ss。
 * - 通过 JsonMapperBuilderCustomizer 增强 Spring Boot 默认配置。
 */
@Configuration
public class JacksonConfig {

    private static final DateTimeFormatter DATETIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Bean
    public JsonMapperBuilderCustomizer xcmsJacksonCustomizer() {
        return builder -> {
            SimpleModule module = new SimpleModule("xcms");
            module.addSerializer(Long.class, ToStringValueSerializer.forType(Long.class));
            module.addSerializer(Long.TYPE, ToStringValueSerializer.forType(Long.class));
            module.addSerializer(BigInteger.class, ToStringValueSerializer.forType(BigInteger.class));
            module.addSerializer(BigDecimal.class, ToStringValueSerializer.forType(BigDecimal.class));
            module.addSerializer(LocalDateTime.class, new ValueSerializer<LocalDateTime>() {
                @Override
                public void serialize(LocalDateTime value, JsonGenerator gen,
                                      SerializationContext ctx) {
                    gen.writeString(value.format(DATETIME_FORMATTER));
                }
            });
            builder.addModule(module);
        };
    }

    /**
     * 将值 toString 后写为字符串的通用序列化器。
     */
    private static class ToStringValueSerializer<T> extends ValueSerializer<T> {

        private final Class<T> type;

        private ToStringValueSerializer(Class<T> type) {
            this.type = type;
        }

        static <T> ToStringValueSerializer<T> forType(Class<T> type) {
            return new ToStringValueSerializer<>(type);
        }

        @Override
        public void serialize(T value, JsonGenerator gen, SerializationContext ctx) {
            gen.writeString(value.toString());
        }

        @Override
        public Class<?> handledType() {
            return type;
        }
    }
}
