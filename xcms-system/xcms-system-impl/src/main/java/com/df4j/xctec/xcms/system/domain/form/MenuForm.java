package com.df4j.xctec.xcms.system.domain.form;

import com.df4j.xctec.xcms.common.jpa.form.BaseTreeForm;
import lombok.Data;

@Data
public class MenuForm extends BaseTreeForm {

    private String menuName;

    private String menuType;

    private String routePath;

    private String component;

    private String icon;

    private String permission;

    private String visibleStatus;

    private String enableStatus;
}
