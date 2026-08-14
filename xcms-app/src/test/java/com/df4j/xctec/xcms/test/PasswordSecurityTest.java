package com.df4j.xctec.xcms.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * #9 密码安全：证明
 *  - 库中密码非明文（BCrypt 哈希），且能通过 onBeforePersist 在落库路径加密；
 *  - change-password 校验旧密码（错误旧密码被拒）；
 *  - 空/空白新密码被拒（400）；
 *  - 修改后新密码生效、旧密码失效（需重新登录）；
 *  - 「空密码不覆盖」：edit 时传空白密码不应把已有哈希清空（当前实现疑似违反，见断言）。
 *
 * 注：经 API 新建用户的 BCrypt 加密验证由 TenantIsolationTest.create_stampsTenantIdFromContext
 * 及 SmokeTest 佐证（create 路径已不再 500）。
 */
public class PasswordSecurityTest extends IntegrationTestBase {

    private Long uid;
    private static final String OLD = "Old@123";
    private static final String NEW = "New@456";

    @BeforeEach
    void setUp() {
        cleanAll();
        uid = seedUser(1L, "alice", OLD, List.of()).getId();
    }

    @Test
    void storedPassword_isBcryptHash_notPlaintext() {
        var u = userRepository.findById(uid).orElseThrow();
        String pwd = u.getPassword();
        assertThat(pwd).matches("\\$2[ab]\\$.*"); // BCrypt 前缀
        assertThat(pwd).isNotEqualTo(OLD);                       // 非明文
        assertThat(passwordEncoder.matches(OLD, pwd)).isTrue();  // 可校验通过
        assertThat(passwordEncoder.matches("wrong", pwd)).isFalse();
    }

    @Test
    void changePassword_withCorrectOld_storesBcryptAndRotates() throws Exception {
        String token = login(1L, "alice", OLD);
        String body = "{\"oldPassword\":\"" + OLD + "\",\"newPassword\":\"" + NEW + "\"}";
        var resp = authRequest(token, 1L, HttpMethod.POST, "/api/system/user/change-password", body);
        assertThat(resp.getStatusCode().value()).isEqualTo(200);

        var u = userRepository.findById(uid).orElseThrow();
        String pwd = u.getPassword();
        assertThat(pwd).matches("\\$2[ab]\\$.*");
        assertThat(passwordEncoder.matches(NEW, pwd)).isTrue();   // 新密码已加密落库
        assertThat(passwordEncoder.matches(OLD, pwd)).isFalse(); // 旧密码已失效

        // 新密码可登录，旧密码不可登录
        assertThat(login(1L, "alice", NEW)).isNotBlank();
        assertThat(throwingLogin(1L, "alice", OLD)).isTrue(); // 旧密码登录应失败
    }

    @Test
    void changePassword_withWrongOld_rejected400() throws Exception {
        String token = login(1L, "alice", OLD);
        String body = "{\"oldPassword\":\"Wrong@000\",\"newPassword\":\"" + NEW + "\"}";
        var resp = authRequest(token, 1L, HttpMethod.POST, "/api/system/user/change-password", body);
        // 约定：业务异常走 errorNo 通道且 HTTP 200（见 GlobalExceptionHandler）
        assertThat(errorNo(resp)).isEqualTo("400");
        assertThat(passwordEncoder.matches(OLD, userRepository.findById(uid).orElseThrow().getPassword())).isTrue();
    }

    @Test
    void changePassword_withBlankNew_rejected400() throws Exception {
        String token = login(1L, "alice", OLD);
        String body = "{\"oldPassword\":\"" + OLD + "\",\"newPassword\":\"\"}";
        var resp = authRequest(token, 1L, HttpMethod.POST, "/api/system/user/change-password", body);
        assertThat(errorNo(resp)).isEqualTo("400");
    }

    @Test
    void edit_withBlankPassword_preservesExistingHash() throws Exception {
        // 缺陷 F 修复后：edit 传空白密码时，不应把已存的 BCrypt 哈希清空（空密码可登录 = 高危）
        String token = login(1L, "alice", OLD);
        String body = "{\"id\":" + uid + ",\"username\":\"alice\",\"nickname\":\"Alice2\",\"password\":\"\"}";
        var resp = authRequest(token, 1L, HttpMethod.POST, "/api/system/user/edit", body);
        assertThat(resp.getStatusCode().value()).isEqualTo(200);

        var u = userRepository.findById(uid).orElseThrow();
        String pwd = u.getPassword();
        // 原哈希仍在，仍可用旧密码登录（空白密码未覆盖）
        assertThat(pwd).matches("\\$2[ab]\\$.*");
        assertThat(passwordEncoder.matches(OLD, pwd)).isTrue();
        // 且 nickname 已更新，证明 edit 本身成功
        assertThat(u.getNickname()).isEqualTo("Alice2");
    }

    /** 登录失败返回 true（用于断言旧密码失效）。 */
    private boolean throwingLogin(Long tenantId, String username, String rawPassword) {
        try {
            login(tenantId, username, rawPassword);
            return false;
        } catch (IllegalStateException e) {
            return true;
        } catch (Exception e) {
            return true;
        }
    }
}
