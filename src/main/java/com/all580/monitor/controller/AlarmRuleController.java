package com.all580.monitor.controller;

import com.all580.monitor.dto.Result;
import com.all580.monitor.entity.TabAlarmRule;
import com.all580.monitor.service.AlarmRuleService;
import com.all580.monitor.task.AlarmRuleTimer;
import com.github.pagehelper.PageHelper;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: 报警规则API
 * @date 2018/5/16 15:12
 */
@Api(tags = "报警规则接口")
@RestController
@CrossOrigin(allowCredentials = "true")
@RequestMapping("api/alarm/rule")
public class AlarmRuleController {
    @Autowired
    private AlarmRuleService alarmRuleService;
    @Autowired
    private AlarmRuleTimer alarmRuleTimer;

    @ApiOperation(value = "获取报警规则列表", notes = "如果传入page参数则返回value为PageInfo", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "spot", value = "景区ID"),
            @ApiImplicitParam(name = "app", value = "应用ID"),
            @ApiImplicitParam(name = "name", value = "规则名称(模糊匹配)"),
            @ApiImplicitParam(name = "alarm", value = "是否正在报警"),
            @ApiImplicitParam(name = "status", value = "状态"),
            @ApiImplicitParam(name = "pageNum", value = "页总数"),
            @ApiImplicitParam(name = "pageSize", value = "页总数")
    })
    @GetMapping("list")
    public Result<?> list(Integer spot, Integer app, String name, Boolean alarm, Boolean status, Integer pageNum, Integer pageSize) {
        Object value;
        if (pageNum != null && pageSize != null) {
            value = PageHelper.startPage(pageNum, pageSize).doSelectPageInfo(() -> alarmRuleService.list(spot, app, name, alarm, status));
        } else {
            value = alarmRuleService.list(spot, app, name, alarm, status);
        }
        return Result.builder().code(Result.SUCCESS).value(value).build();
    }

    @ApiOperation(value = "新增报警规则", produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping("add")
    public Result<?> add(@ApiParam(required = true) @RequestBody TabAlarmRule rule) {
        int ret = alarmRuleService.save(rule);
        if (ret <= 0) {
            return Result.fail();
        }
        alarmRuleTimer.add(rule.getId());
        return Result.ok();
    }

    @ApiOperation(value = "修改警规则", produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping("update")
    public Result<?> update(@ApiParam(required = true) @RequestBody TabAlarmRule rule) {
        rule.setCurrentCount(null).setLastHistory(null);
        int ret = alarmRuleService.updateNotNull(rule);
        if (ret <= 0) {
            return Result.fail();
        }
        alarmRuleTimer.update(rule.getId());
        return Result.ok();
    }

    @ApiOperation(value = "删除警规则", produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping("remove/{id}")
    public Result<?> remove(@ApiParam(required = true) @PathVariable int id) {
        int ret = alarmRuleService.delete(id);
        if (ret <= 0) {
            return Result.fail();
        }
        alarmRuleTimer.update(id);
        return Result.ok();
    }
}
