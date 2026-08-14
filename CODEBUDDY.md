# CODEBUDDY.md

This file provides guidance to CodeBuddy Code when working with code in this repository.

## 项目概览

XCMS 是一套支持**级联（层级）租户**的后台租户管理系统，采用**模块化单体（Modular Monolith）**架构（Java 21、Spring Boot 4.0.7、Spring Security 7），结构上预留了按需拆分为微服务的能力。本仓库**不含前端**，是一个纯 REST API 后端。

基础包名：`com.df4j.xctec.xcms`

## 构建与运行

构建系统为 Maven。**没有 Maven 包装器（`mvnw`）**，直接使用系统安装的 `mvn`（需 Maven 3.x 与 JDK 21）。父 POM 使用 `flatten-maven-plugin`，统一版本 `${revision}` = `1.0.0-SNAPSHOT`。

```bash
# 全量清理构建 + 跑测试 + 安装所有模块
mvn clean install

# 跳过测试构建
mvn clean install -DskipTests

# 构建单个模块及其依赖
mvn -pl xcms-app -am install

# 运行应用（dev 环境，端口 8080）
mvn -pl xcms-app spring-boot:run
# 或打包后运行 fat jar：
mvn -pl xcms-app -am clean package && java -jar xcms-app/target/xcms-app-1.0.0-SNAPSHOT.jar
```

配置仅存在于 `xcms-app/src/main/resources/`：`application.yml`（激活 dev 环境，端口 8080）与 `application-dev.yml`（H2 文件库 `./data/xcms`，H2 控制台在 `/h2-console`）。生产目标为 MySQL 8（`ddl-auto: validate`），但目前还没有 `application-prod.yml`。

## 测试

Spring Boot 父 POM 自带 JUnit 5（Jupiter）+ Surefire 插件。**目前仓库内没有任何测试，也没有测试作用域依赖** —— 写测试前需先给对应模块引入 `spring-boot-starter-test`。

```bash
# 跨所有模块运行测试（当前无任何测试）
mvn test

# 仅运行单个模块测试
mvn -pl xcms-system test

# 运行单个测试类或单个方法
mvn test -Dtest=UserRepositoryTest
mvn test -Dtest=UserServiceTest#shouldCreateUser
```

## 架构

### Maven 模块布局

```
xctec-xcms（父 POM，packaging=pom）
├── xcms-core       基础内核：context、dto、vo、mapper、exception、tree、utils（无内部依赖）
├── xcms-common     横切基础设施：安全、JPA 通用基类、Web 异常处理、配置
├── xcms-system     {api, impl} —— 系统/RBAC 域
├── xcms-auth       {api, impl} —— 认证域
├── xcms-portal     {api, impl} —— 当前用户/门户域
└── xcms-app        唯一可运行的 Spring Boot 应用（主类 + 全部资源）
```

依赖方向：
`xcms-core` → `xcms-common` → `xcms-*-api` → `xcms-*-impl` → `xcms-app`。
领域模块是**两级结构**：每个 `xcms-<domain>` 自身是父 POM，聚合 `<domain>-api` 与 `<domain>-impl`。`-api` 模块只放契约（接口 + DTO/VO/Query）；`-impl` 模块放控制器、实体、仓库、服务、转换器。

关键解耦规则：`xcms-portal-impl` 依赖 `xcms-system-api`，**而非** `xcms-system-impl` —— 它只消费菜单契约，不引入实现。这是为将来微服务拆分预留的接缝。同样地，`common` 中定义了端口接口（`AuthUserProvider`、`UserMenuService`），由 `system` 实现、`auth`/`portal` 消费。

### 分层结构（位于 `*-impl` 内）
```
controller/   REST 入口，常继承 BaseController
domain/
  converter/  MapStruct 映射器（<Entity>Converter）
  dto/        响应/内部 DTO
  entity/     JPA 实体
  form/       新增/修改入参
  query/      查询条件
repository/   Spring Data JPA 仓库
service/      业务服务（BaseService 子类 + 端口实现）
```

### 重要约定
- **契约与实现分离**：领域端点以带注解的接口形式声明在 `*-api` 中（如 `LoginApi` 带 `@PostMapping("/auth/login")`），由 `*-impl` 中的 `@RestController` 类实现（如 `LoginController implements LoginApi`）。通用 CRUD 控制器继承 `BaseController`，自动挂载 `/page`、`/list`、`/create`、`/edit`、`/del`、`/delAll`。
- **基类**（前缀 `Base`）：`BaseEntity` → `BaseAuditableEntity` / `BaseTreeEntity`，均实现 `TenantScoped`（提供 `getTenantId/setTenantId`）。另有 `BaseRepository`、`BaseService`/`BaseTreeService`、`BaseController`/`BaseTreeController`、`BaseDto`/`BaseForm` 等。
- **租户隔离**：每个请求通过 `X-Tenant-Id` 请求头 → `TenantContextFilter` → `TenantContextUtils`（ThreadLocal）设置租户，请求结束后清空。级联租户由 `BaseTreeEntity` + `TenantInfo`（`sys_tenant_info`，树形 `node_code`）建模。
- **认证**：无状态（Spring Security `sessionManagement(STATELESS)`）。`Authorization` 头中的 Bearer Token → `XcmsBearerTokenAuthenticationFilter` → `TokenManager`（当前为 `MemoryTokenManager`）。未认证访问抛出 `BizException("401", ...)`。公开/忽略路径在 `application.yml` 的 `xcms.security.permit-all-matchers` / `ignore-matchers` 中配置。
- **QueryDSL**：集中配置在父 POM 的 `maven-compiler-plugin` 的 `annotationProcessorPaths`（lombok + querydsl-apt + mapstruct）。Q 类型（`QUser`、`QMenu`）编译期生成；服务中用 `JPQLQueryFactory` 与 `private final QUser q = QUser.user;` 构建查询。
- **映射**：MapStruct，全局 `@MapperConfig`（`GlobalMapperConfig`，`componentModel="spring"`、`nullValuePropertyMappingStrategy=IGNORE`）；转换器使用 `@Mapper(config = GlobalMapperConfig.class)`。
- **统一响应**：`ResultVo<T>`（`errorNo`/`errorMsg`/`data`，默认成功为 `"0"/"success"`）经 `ResultUtils` 返回；分页用 `PageVo<D>` + `PageQuery`。业务错误统一使用 `com.df4j.xctec.xcms.core.exception.BizException`，由 common 的 Web 异常处理器翻译。
- **依赖注入**：服务使用 Lombok `@RequiredArgsConstructor`（构造器注入）；通用基类使用 `@Setter(onMethod_ = @Autowired)`（字段注入）。

### 请求生命周期（以 User 为例）
```
请求 → TenantContextFilter（读取 X-Tenant-Id）→ TokenContextFilter
      → XcmsBearerTokenAuthenticationFilter
      → UserController（/api/system/user，继承 BaseController）
      → UserService（继承 BaseService，QueryDSL 经 QUser + JPQLQueryFactory）
      → UserRepository（继承 BaseRepository，JPA + @EntityGraph）
      → UserConverter（MapStruct）-> UserDto
      → ResultUtils.success(ResultVo<PageVo<UserDto>>)
```

## 备注
- 未配置任何 lint/格式化工具（Checkstyle、Spotless 等）—— 没有可运行的 lint 命令。
- 当前没有 `README.md`。存在一个拼写错误的 `READMD.md`，内含部分技术栈表格，建议改名并补全。
