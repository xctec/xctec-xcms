package com.df4j.xctec.xcms.web.sort;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 排序白名单解析器。
 *
 * 各 impl 模块通过 register(field, path) 注册白名单（DTO 字段名 → QueryDSL 路径表达式）。
 * resolve(orderBy, defaultSpecifier) 解析前端传入的排序字段：
 * - 命中白名单：返回对应的 OrderSpecifier
 * - 未命中：返回默认排序，并记 warn 日志
 *
 * SQL 不拼接外部字符串，从根上防注入。
 */
public class SortResolver {

    private static final Logger log = LoggerFactory.getLogger(SortResolver.class);

    private final Map<String, ComparableExpressionBase<?>> whitelist = new ConcurrentHashMap<>();

    /**
     * 注册排序白名单。由各 impl 模块在 @Configuration 中调用。
     */
    public void register(String field, ComparableExpressionBase<?> path) {
        whitelist.put(field, path);
    }

    /**
     * 批量注册。
     */
    public void registerAll(Map<String, ComparableExpressionBase<?>> mappings) {
        whitelist.putAll(mappings);
    }

    /**
     * 解析 orderBy 字符串为 OrderSpecifier。
     *
     * @param orderBy           前端传入，格式 "field,direction"（direction: asc/desc，默认 asc）
     * @param defaultSpecifier  未命中白名单时的默认排序
     * @return 排序表达式
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public OrderSpecifier<?> resolve(String orderBy, OrderSpecifier<?> defaultSpecifier) {
        if (!StringUtils.hasText(orderBy)) {
            return defaultSpecifier;
        }
        String[] parts = orderBy.split(",");
        if (parts.length == 0 || !StringUtils.hasText(parts[0])) {
            return defaultSpecifier;
        }
        String field = parts[0].trim();
        Order direction = (parts.length > 1 && "desc".equalsIgnoreCase(parts[1].trim()))
                ? Order.DESC : Order.ASC;
        ComparableExpressionBase<?> path = whitelist.get(field);
        if (path == null) {
            log.warn("orderBy field not in whitelist, using default sort: {}", field);
            return defaultSpecifier;
        }
        return new OrderSpecifier(direction, path);
    }
}
