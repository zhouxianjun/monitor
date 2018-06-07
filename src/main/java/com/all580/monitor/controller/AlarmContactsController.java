package com.all580.monitor.controller;

import com.all580.monitor.dto.Result;
import com.all580.monitor.entity.TabAlarmContacts;
import com.all580.monitor.service.AlarmContactsService;
import com.github.pagehelper.PageHelper;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

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

    @ApiOperation(value = "获取联系人列表", notes = "如果传入page参数则返回value为PageInfo", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "groupId", value = "组ID"),
            @ApiImplicitParam(name = "keyword", value = "关键字（模糊匹配）"),
            @ApiImplicitParam(name = "pageNum", value = "页码"),
            @ApiImplicitParam(name = "pageSize", value = "每页条数"),
    })
    @GetMapping("list")
    public Result<?> list(Integer groupId, String keyword, Integer pageNum, Integer pageSize) {
        Object value;
        if (pageNum != null && pageSize != null) {
            value = PageHelper.startPage(pageNum, pageSize).doSelectPageInfo(() -> contactsService.search(groupId, keyword));
        } else {
            value = contactsService.search(groupId, keyword);
        }
        return Result.builder().code(Result.SUCCESS).value(value).build();
    }

    @ApiOperation(value = "新增联系人", produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping("add")
    public Result<?> add(@ApiParam(required = true) @RequestBody TabAlarmContacts contacts) {
        int ret = contactsService.save(contacts);
        return ret > 0 ? Result.ok() : Result.fail();
    }

    @ApiOperation(value = "修改联系人", produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping("update")
    public Result<?> update(@ApiParam(required = true) @RequestBody TabAlarmContacts contacts) {
        int ret = contactsService.updateNotNull(contacts);
        return ret > 0 ? Result.ok() : Result.fail();
    }

    @ApiOperation(value = "删除联系人", produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping("remove")
    public Result<?> remove(@ApiParam(required = true) @RequestBody TabAlarmContacts contacts) {
        int ret = contactsService.delete(contacts.getId());
        return ret > 0 ? Result.ok() : Result.fail();
    }
}
