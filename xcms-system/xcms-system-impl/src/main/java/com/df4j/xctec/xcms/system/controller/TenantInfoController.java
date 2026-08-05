package com.df4j.xctec.xcms.system.controller;

import com.df4j.xctec.xcms.common.jpa.controller.BaseTreeController;
import com.df4j.xctec.xcms.system.domain.dto.TenantInfoDto;
import com.df4j.xctec.xcms.system.domain.form.TenantInfoForm;
import com.df4j.xctec.xcms.system.domain.query.TenantInfoQuery;
import com.df4j.xctec.xcms.system.service.TenantInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/system/tenant-info")
public class TenantInfoController
        extends BaseTreeController<TenantInfoDto, TenantInfoForm, TenantInfoQuery, TenantInfoService> {

}
