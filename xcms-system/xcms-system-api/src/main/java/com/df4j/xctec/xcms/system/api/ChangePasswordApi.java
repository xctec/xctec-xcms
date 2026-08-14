package com.df4j.xctec.xcms.system.api;

import com.df4j.xctec.xcms.core.vo.ResultVo;
import com.df4j.xctec.xcms.system.api.dto.ChangePasswordRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 修改密码接口契约（定义在 system-api，实现在 system-impl）。
 * <p>
 * 后续拆分为独立微服务时，本接口即可作为 Feign/OpenAPI 契约直接使用。
 */
public interface ChangePasswordApi {

    @PostMapping("/api/system/user/change-password")
    ResultVo<Void> changePassword(@RequestBody ChangePasswordRequest request);
}
