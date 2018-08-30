package com.all580.monitor.controller;

import com.all580.monitor.dto.Result;
import com.all580.monitor.entity.TabUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description:
 * @date 2018/8/30 11:37
 */
@Api(tags = "开放接口")
@RestController
@CrossOrigin(allowCredentials = "true")
@RequestMapping("open")
public class OpenController {

    @ApiOperation(value = "登录", produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping("login")
    public Result<?> login(@ApiParam(required = true) @RequestBody TabUser user) {
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(user.getUsername(), user.getPassword());
        subject.login(token);
        return Result.ok();
    }

    @ApiOperation(value = "登出", produces = MediaType.APPLICATION_JSON_VALUE)
    @GetMapping("logout")
    public Result<?> logout() {
        SecurityUtils.getSubject().logout();
        return Result.ok();
    }
}
