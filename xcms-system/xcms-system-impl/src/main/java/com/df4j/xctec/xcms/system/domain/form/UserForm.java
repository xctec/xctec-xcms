package com.df4j.xctec.xcms.system.domain.form;

import com.df4j.xctec.xcms.common.jpa.form.BaseForm;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserForm extends BaseForm {

    private String username;

    private String password;

    private String nickname;

    private String email;

    private String phone;

    private String avatar;

    private Long orgUnitId;

    private String enableStatus;
}
