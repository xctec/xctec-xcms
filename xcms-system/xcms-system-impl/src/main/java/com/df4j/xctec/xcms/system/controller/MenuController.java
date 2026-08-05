package com.df4j.xctec.xcms.system.controller;

import com.df4j.xctec.xcms.common.jpa.controller.BaseTreeController;
import com.df4j.xctec.xcms.system.domain.dto.MenuDto;
import com.df4j.xctec.xcms.system.domain.form.MenuForm;
import com.df4j.xctec.xcms.system.domain.query.MenuQuery;
import com.df4j.xctec.xcms.system.service.MenuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/system/menu")
public class MenuController extends BaseTreeController<MenuDto, MenuForm, MenuQuery, MenuService> {

}
