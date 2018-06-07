package com.all580.monitor.controller;

import com.all580.monitor.dto.Result;
import com.all580.monitor.entity.TabAlarmContactsGroup;
import com.all580.monitor.service.AlarmContactsGroupService;
import com.all580.monitor.vo.AlarmContactsGroupVo;
import com.github.pagehelper.PageHelper;
import io.swagger.annotations.*;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

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

    @ApiOperation(value = "新增联系人组", produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping("add")
    public Result<?> add(@ApiParam(required = true) @RequestBody AlarmContactsGroupVo vo) {
        return groupService.add(vo.getGroup(), vo.getContacts());
    }

    @ApiOperation(value = "修改联系人组", produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping("update")
    public Result<?> update(@ApiParam(required = true) @RequestBody AlarmContactsGroupVo vo) {
        return groupService.update(vo.getGroup(), vo.getContacts());
    }

    @ApiOperation(value = "删除联系人组", produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping("remove")
    public Result<?> remove(@ApiParam(required = true) @RequestBody TabAlarmContactsGroup group) {
        int ret = groupService.delete(group.getId());
        return ret > 0 ? Result.ok() : Result.fail();
    }
}
