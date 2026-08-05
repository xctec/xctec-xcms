package com.df4j.xctec.xcms.system.domain.form;

import com.df4j.xctec.xcms.common.jpa.form.BaseTreeForm;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrgUnitForm extends BaseTreeForm {

    private Long tenantId;

    private String orgName;

    private String enableStatus;
}
