package com.df4j.xctec.xcms.core.utils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;


@Slf4j
public class ResponseUtils {

    public static void writeJson(HttpServletResponse response, Object result) {
        writeJson(response, response, HttpStatus.OK);
    }

    public static void writeJson(HttpServletResponse response, Object result, HttpStatus httpStatus) {
        String resultStr = null;
        try {
            response.setStatus(httpStatus.value());
            response.setCharacterEncoding(StandardCharsets.UTF_8.toString());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            resultStr = JsonUtils.stringify(result);
            response.getWriter().write(resultStr);
        } catch (Exception e) {
            log.warn("写入响应异常, status: {}, str: {}", httpStatus, resultStr);
        }
    }

    public static void writeHeaders(HttpServletResponse response, Map<String, String> headers) {
        if (headers == null || headers.isEmpty()) {
            return;
        }
        for (String key : headers.keySet()) {
            response.addHeader(key, headers.get(key));
        }
    }

    public static void writeCookies(HttpServletResponse response, List<Cookie> cookies) {
        if (cookies == null || cookies.isEmpty()) {
            return;
        }
        cookies.forEach(response::addCookie);
    }
}
