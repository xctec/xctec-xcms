package com.df4j.xctec.xcms.system.controller;

import com.df4j.xctec.xcms.common.jpa.controller.BaseController;
import com.df4j.xctec.xcms.system.domain.dto.UserDto;
import com.df4j.xctec.xcms.system.domain.form.UserForm;
import com.df4j.xctec.xcms.system.domain.query.UserQuery;
import com.df4j.xctec.xcms.system.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/system/user")
public class UserController extends BaseController<UserDto, UserForm, UserQuery, UserService> {

}
