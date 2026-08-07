package com.df4j.xctec.xcms.core.utils;

import tools.jackson.databind.json.JsonMapper;

public class JsonUtils {

    private static final JsonMapper jsonMapper = JsonMapper.builder()
            .build();

    public static String stringify(Object obj) {
        return jsonMapper.writeValueAsString(obj);
    }

    public static <T> T parse(String str, Class<T> returnType) {
        return jsonMapper.readValue(str, returnType);
    }
}
