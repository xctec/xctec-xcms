package com.df4j.xctec.xcms.core.result;

import java.text.MessageFormat;

/**
 * 错误码抽象。格式约定：{模块}.{领域}.{错误}。
 */
public interface ErrorCode {

    String code();

    String messageTemplate();

    default String format(Object... args) {
        return (args == null || args.length == 0)
                ? messageTemplate()
                : MessageFormat.format(messageTemplate(), args);
    }
}
