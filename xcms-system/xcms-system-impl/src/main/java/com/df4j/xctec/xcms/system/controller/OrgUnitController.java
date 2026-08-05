package com.df4j.xctec.xcms.system.controller;

import com.df4j.xctec.xcms.common.jpa.controller.BaseTreeController;
import com.df4j.xctec.xcms.system.domain.dto.OrgUnitDto;
import com.df4j.xctec.xcms.system.domain.form.OrgUnitForm;
import com.df4j.xctec.xcms.system.domain.query.OrgUnitQuery;
import com.df4j.xctec.xcms.system.service.OrgUnitService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/system/org-unit")
public class OrgUnitController extends BaseTreeController<OrgUnitDto, OrgUnitForm, OrgUnitQuery, OrgUnitService> {

}
