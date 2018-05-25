package com.all580.monitor.controller;

import cn.hutool.core.lang.Assert;
import com.all580.monitor.dto.Result;
import com.all580.monitor.entity.TabAlarmContacts;
import com.all580.monitor.service.AlarmContactsService;
import com.github.pagehelper.PageHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author panyi on 18-5-24.
 */
@Api(tags = "联系人接口")
@RestController
@CrossOrigin(allowCredentials = "true")
@RequestMapping("api/contacts")
public class AlarmContactsController {
    @Autowired
    private AlarmContactsService contactsService;

    /**
     * 获取联系人列表
     *
     * @param name 姓名
     * @return
     */
    @ApiOperation(value = "获取联系人列表", notes = "如果传入page参数则返回value为PageInfo", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", value = "姓名(模糊匹配)"),
            @ApiImplicitParam(name = "pageNum", value = "页码"),
            @ApiImplicitParam(name = "pageSize", value = "每页条数"),
    })
    @GetMapping("list")
    public Result<?> list(String name, Integer pageNum, Integer pageSize) {
        Object value;
        if (pageNum != null && pageSize != null) {
            value = PageHelper.startPage(pageNum, pageSize).doSelectPageInfo(() -> contactsService.all());
        } else {
            value = contactsService.all();
        }
        return Result.builder().code(Result.SUCCESS).value(value).build();
    }

    /**
     * 通过组ID获取联系人列表
     *
     * @param groupId 姓名
     * @return
     */
    @ApiOperation(value = "通过组ID获取联系人列表", notes = "不分页，返回全部成员", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "groupId", value = "组ID"),
    })
    @GetMapping("listByGroup")
    public Result<?> listByGroup(Integer groupId) {
        Assert.notNull(groupId, "请指定组");
        List<TabAlarmContacts> value = contactsService.listByGroup(groupId);
        return Result.builder().code(Result.SUCCESS).value(value).build();
    }
}
