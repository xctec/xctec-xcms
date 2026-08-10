package com.df4j.xctec.xcms.system.domain.form;

import com.df4j.xctec.xcms.common.jpa.form.BaseForm;
import lombok.Data;

@Data
public class UserForm extends BaseForm {

    private String username;

    private String password;

    private String nickname;

    private String email;

    private String mobile;

    private String avatar;

    private Long orgUnitId;

    private String enableStatus;
}
