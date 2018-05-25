package com.all580.monitor.controller;

import com.all580.monitor.dto.Result;
import com.all580.monitor.service.AlarmContactsGroupService;
import com.github.pagehelper.PageHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author panyi on 18-5-25.
 */
@Api(tags = "联系人组接口")
@RestController
@CrossOrigin(allowCredentials = "true")
@RequestMapping("api/contacts/group")
public class AlarmContactsGroupController {
    @Resource
    private AlarmContactsGroupService groupService;

    /**
     * 获取联系人组列表
     *
     * @param pageNum 页码
     * @return
     */
    @ApiOperation(value = "获取联系人组列表", notes = "如果传入page参数则返回value为PageInfo", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "页码"),
            @ApiImplicitParam(name = "pageSize", value = "每页条数"),
    })
    @GetMapping("list")
    public Result<?> list(Integer pageNum, Integer pageSize) {
        Object value;
        if (pageNum != null && pageSize != null) {
            value = PageHelper.startPage(pageNum, pageSize).doSelectPageInfo(() -> groupService.all());
        } else {
            value = groupService.all();
        }
        return Result.builder().code(Result.SUCCESS).value(value).build();
    }
}
