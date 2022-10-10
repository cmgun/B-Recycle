package com.brecycle.controller;

import com.brecycle.common.Response;
import com.brecycle.entity.dto.UserInfo;
import com.brecycle.entity.dto.UserLoginParam;
import com.brecycle.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户模块
 *
 * @author cmgun
 */
@Api(tags = "用户模块")
@RestController
@RequestMapping("user")
public class UserController {

    @Autowired
    private UserService userService;

    @ApiOperation("用户登录")
    @PostMapping("/login")
    Response<UserInfo> login(@RequestBody @ApiParam(value = "参数", required = true) UserLoginParam param) throws Exception {
        UserInfo userInfo = userService.login(param.getUserName(), param.getPassword());
        return Response.success("登录成功", userInfo);
    }
}
