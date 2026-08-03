package com.df4j.xctec.xcms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * XCMS 应用启动类。落在根包，默认组件扫描覆盖全部模块。
 */
@EnableJpaAuditing
@SpringBootApplication
public class XcmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(XcmsApplication.class, args);
    }
}
