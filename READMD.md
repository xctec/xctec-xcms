# XCMS 租户管理系统

---

## 一、项目定位与核心原则

XCMS 是一套支持**级联租户**的后端管理系统，采用模块化单体架构，可根据需要拆分模块为微服务。

---

## 二、技术栈

| 类别 | 选型 | 说明 |
|---|---|---|
| JDK | 21 | 不启用虚拟线程，但代码写法兼容 |
| 框架 | Spring Boot 4.x | |
| 安全 | Spring Security 7 | 自定义 Token 过滤器链 |
| 持久层 | Spring Data JPA + Hibernate 7 | 开发阶段 `ddl-auto: update` 加速迭代；生产阶段 `ddl-auto: validate`，表结构由 DDL 脚本管理（生产 DDL 方案后续阶段完善） |
| 查询 | QueryDSL `io.github.openfeign.querydsl:7.5` | 原生 Jakarta，无 classifier 困扰 |
| 映射 | MapStruct 1.6.x | 全局 `@MapperConfig` |
| 数据库 | H2（`MODE=MySQL`）开发 / MySQL 8 生产 | |

---