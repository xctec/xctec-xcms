package com.df4j.xctec.xcms.test;

import com.df4j.xctec.xcms.system.domain.entity.Menu;
import com.df4j.xctec.xcms.system.domain.entity.Role;
import com.df4j.xctec.xcms.system.domain.entity.TenantInfo;
import com.df4j.xctec.xcms.system.domain.entity.User;
import com.df4j.xctec.xcms.system.repository.MenuRepository;
import com.df4j.xctec.xcms.system.repository.RoleRepository;
import com.df4j.xctec.xcms.system.repository.TenantInfoRepository;
import com.df4j.xctec.xcms.system.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.springframework.web.client.RestClient.create;

/**
 * 聚合集成测试基类：启动完整 xcms-app（真实 Servlet 容器，随机端口），使用 RestClient 走完整
 * Spring Security 过滤器链（TenantContextFilter / XcmsBearerTokenAuthenticationFilter /
 * TenantValidationFilter / ExceptionTranslationFilter），用真实 HTTP 请求验证租户隔离、密码安全
 * 与 RBAC 授权。
 *
 * <p>种子数据直接经 Repository 落库（绕过 BaseService 的租户 stamp / 审计字段注入，
 * 因此种子必须自行补齐 createTime/updateTime/createBy 等 NOT NULL 审计列）。</p>
 */
@SpringBootTest(classes = com.df4j.xctec.xcms.Main.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class IntegrationTestBase {

    @LocalServerPort
    protected int port;

    @Autowired
    protected UserRepository userRepository;
    @Autowired
    protected RoleRepository roleRepository;
    @Autowired
    protected MenuRepository menuRepository;
    @Autowired
    protected TenantInfoRepository tenantInfoRepository;
    @Autowired
    protected PasswordEncoder passwordEncoder;

    protected final RestClient rest = create();
    protected final ObjectMapper objectMapper = new ObjectMapper();

    protected String baseUrl() {
        return "http://localhost:" + port;
    }

    // ---------- 认证辅助 ----------

    /** 用 X-Tenant-Id + 用户名密码登录，返回 accessToken；失败抛异常（由调用方断言）。 */
    @SuppressWarnings("unchecked")
    protected String login(Long tenantId, String username, String rawPassword) {
        String body = "{\"username\":\"" + username + "\",\"password\":\"" + rawPassword + "\"}";
        ResponseEntity<String> resp = rest.post().uri(baseUrl() + "/auth/login")
                .header("X-Tenant-Id", String.valueOf(tenantId))
                .contentType(MediaType.APPLICATION_JSON).body(body)
                .retrieve().toEntity(String.class);
        if (resp.getStatusCode().isError() || resp.getBody() == null) {
            throw new IllegalStateException("登录失败 tenant=" + tenantId + " user=" + username
                    + " status=" + resp.getStatusCode() + " body=" + resp.getBody());
        }
        Map<String, Object> m = readMap(resp.getBody());
        Object data = m.get("data");
        if (data instanceof Map) {
            Object at = ((Map<?, ?>) data).get("accessToken");
            if (at != null) {
                return at.toString();
            }
        }
        throw new IllegalStateException("登录响应中未找到 accessToken: " + resp.getBody());
    }

    /** 带 Bearer + X-Tenant-Id 的请求（tenantId 为 null 时不带 X-Tenant-Id 头）。 */
    protected ResponseEntity<String> authRequest(String token, Long tenantId, HttpMethod method,
                                                 String path, String body) {
        RestClient.RequestBodySpec spec = rest.method(method).uri(baseUrl() + path)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON);
        if (tenantId != null) {
            spec = spec.header("X-Tenant-Id", String.valueOf(tenantId));
        }
        try {
            return spec.body(body == null ? "" : body).retrieve().toEntity(String.class);
        } catch (org.springframework.web.client.HttpStatusCodeException ex) {
            // RestClient 对 4xx/5xx 抛异常；原样包装为 ResponseEntity 以便断言
            return ResponseEntity.status(ex.getStatusCode())
                    .body(ex.getResponseBodyAsString());
        }
    }

    /** 允许传入任意（含非法）X-Tenant-Id 原始字符串，用于畸形值/注入边界测试。 */
    protected ResponseEntity<String> rawAuthRequest(String token, String rawTenantHeader,
                                                    String path, String body) {
        try {
            return rest.post().uri(baseUrl() + path)
                    .header("Authorization", "Bearer " + token)
                    .header("X-Tenant-Id", rawTenantHeader)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body == null ? "" : body)
                    .retrieve().toEntity(String.class);
        } catch (org.springframework.web.client.HttpStatusCodeException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getResponseBodyAsString());
        }
    }

    // ---------- 响应解析 ----------

    @SuppressWarnings("unchecked")
    protected Map<String, Object> readMap(String body) {
        try {
            return objectMapper.readValue(body, Map.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected String errorNo(ResponseEntity<String> resp) {
        Map<String, Object> m = readMap(resp.getBody());
        Object en = m.get("errorNo");
        return en == null ? null : en.toString();
    }

    @SuppressWarnings("unchecked")
    protected Map<String, Object> dataMapOf(ResponseEntity<String> resp) {
        Map<String, Object> m = readMap(resp.getBody());
        Object d = m.get("data");
        if (d instanceof Map) {
            return (Map<String, Object>) d;
        }
        return m; // 非 Map（如分页场景 data 本身就是 PageVo）时返回整体，便于取 total
    }

    @SuppressWarnings("unchecked")
    protected List<Map<String, Object>> dataListOf(ResponseEntity<String> resp) {
        Map<String, Object> m = readMap(resp.getBody());
        Object d = m.get("data");
        if (d instanceof List) {
            return (List<Map<String, Object>>) d; // 扁平列表（/list）
        }
        if (d instanceof Map dm) {
            Object list = dm.get("list"); // 分页结构 data:{total,list}
            if (list instanceof List) {
                return (List<Map<String, Object>>) list;
            }
        }
        return List.of();
    }

    protected List<String> menuNamesOf(ResponseEntity<String> resp) {
        List<Map<String, Object>> list = dataListOf(resp);
        List<String> names = new java.util.ArrayList<>();
        for (Map<String, Object> item : list) {
            names.add(String.valueOf(item.get("menuName")));
        }
        return names;
    }

    // ---------- 种子数据 ----------

    protected void cleanAll() {
        menuRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();
        tenantInfoRepository.deleteAll();
    }

    protected TenantInfo seedTenant(String name) {
        TenantInfo t = new TenantInfo();
        t.setTenantName(name);
        t.setEnableStatus(1);
        t.setNodeCode("t_" + name);
        t.setParentId(0L);
        t.setTreeLevel(1);
        t.setPath("/0/");
        t.setCodePath("/0/");
        t.setSortNum(1);
        audit(t);
        return tenantInfoRepository.save(t);
    }

    protected Role seedRole(Long tenantId, String roleCode, String roleName) {
        Role r = new Role();
        r.setTenantId(tenantId);
        r.setRoleCode(roleCode);
        r.setRoleName(roleName);
        r.setEnableStatus("ENABLED");
        audit(r);
        return roleRepository.save(r);
    }

    protected User seedUser(Long tenantId, String username, String rawPassword, List<Role> roles) {
        User u = new User();
        u.setTenantId(tenantId);
        u.setUsername(username);
        u.setPassword(passwordEncoder.encode(rawPassword));
        u.setNickname(username);
        u.setEnableStatus("ENABLED");
        u.setRoles(roles);
        audit(u);
        return userRepository.save(u);
    }

    protected Menu seedMenu(Long tenantId, String name, String menuType, String permission) {
        Menu m = new Menu();
        m.setTenantId(tenantId);
        m.setMenuName(name);
        m.setMenuType(menuType);
        m.setPermission(permission); // null => 公开菜单
        m.setVisibleStatus("1");
        m.setEnableStatus("ENABLED");
        m.setNodeCode("m_" + name);
        m.setParentId(0L);
        m.setTreeLevel(1);
        m.setPath("/0/");
        m.setCodePath("/0/");
        m.setSortNum(1);
        audit(m);
        return menuRepository.save(m);
    }

    /** 补齐 NOT NULL 审计列（应用自身已通过 BaseService.stampAudit 在 create/edit 注入）。 */
    private void audit(Object entity) {
        Instant now = Instant.now();
        if (entity instanceof com.df4j.xctec.xcms.common.jpa.entity.BaseEntity be) {
            be.setCreateBy(0L);
            be.setCreateTime(now);
        }
        if (entity instanceof com.df4j.xctec.xcms.common.jpa.entity.BaseAuditableEntity ae) {
            ae.setUpdateTime(now);
        }
    }
}
