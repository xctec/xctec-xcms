package com.df4j.xctec.xcms.system.controller;

import com.df4j.xctec.xcms.common.jpa.controller.BaseController;
import com.df4j.xctec.xcms.system.domain.dto.RoleDto;
import com.df4j.xctec.xcms.system.domain.form.RoleForm;
import com.df4j.xctec.xcms.system.domain.query.RoleQuery;
import com.df4j.xctec.xcms.system.service.RoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/system/role")
public class RoleController extends BaseController<RoleDto, RoleForm, RoleQuery, RoleService> {

}
