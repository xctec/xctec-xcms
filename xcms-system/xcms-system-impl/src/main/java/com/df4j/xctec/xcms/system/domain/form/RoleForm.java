package com.df4j.xctec.xcms.system.domain.form;

import com.df4j.xctec.xcms.common.jpa.form.BaseForm;
import lombok.Data;

@Data
public class RoleForm extends BaseForm {

    private String roleCode;

    private String roleName;

    private String roleDesc;

    private String enableStatus;
}
