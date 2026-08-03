package com.df4j.xctec.xcms.common.core;

/**
 * 错误码抽象。格式约定：{模块}.{领域}.{错误}。
 */
public interface ErrorCode {

    String code();

    String messageTemplate();

    default String format(Object... args) {
        return (args == null || args.length == 0)
                ? messageTemplate()
                : java.text.MessageFormat.format(messageTemplate(), args);
    }
}
