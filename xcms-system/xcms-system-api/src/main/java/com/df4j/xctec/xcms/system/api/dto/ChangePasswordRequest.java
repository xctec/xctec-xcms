package com.df4j.xctec.xcms.system.api.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 修改密码请求参数。
 */
@Data
public class ChangePasswordRequest implements Serializable {

    /** 旧密码（明文） */
    private String oldPassword;

    /** 新密码（明文） */
    private String newPassword;
}
