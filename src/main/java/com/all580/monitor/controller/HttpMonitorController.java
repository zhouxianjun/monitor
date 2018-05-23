package com.all580.monitor.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.map.MapUtil;
import com.all580.monitor.dto.Result;
import com.all580.monitor.entity.TabAlarmRule;
import com.all580.monitor.entity.TabHttpMonitor;
import com.all580.monitor.service.HttpMonitorService;
import com.all580.monitor.task.HttpHeartbeatTimer;
import com.github.pagehelper.PageHelper;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: HTTP监控API
 * @date 2018/5/22 14:16
 */
@Api(tags = "HTTP监控接口")
@RestController
@CrossOrigin(allowCredentials = "true")
@RequestMapping("api/monitor/http")
public class HttpMonitorController {
    @Autowired
    private HttpMonitorService httpMonitorService;
    @Autowired
    private HttpHeartbeatTimer httpHeartbeatTimer;

    @ApiOperation(value = "获取监控列表", notes = "如果传入page参数则返回value为PageInfo", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "spot", value = "景区ID"),
            @ApiImplicitParam(name = "app", value = "应用ID"),
            @ApiImplicitParam(name = "name", value = "站点名称(模糊匹配)"),
            @ApiImplicitParam(name = "alarm", value = "是否正在报警"),
            @ApiImplicitParam(name = "status", value = "状态"),
            @ApiImplicitParam(name = "pageNum", value = "页总数"),
            @ApiImplicitParam(name = "pageSize", value = "页总数")
    })
    @GetMapping("list")
    public Result<?> list(Integer spot, Integer app, String name, Boolean alarm, Boolean status, Integer pageNum, Integer pageSize) {
        Object value;
        if (pageNum != null && pageSize != null) {
            value = PageHelper.startPage(pageNum, pageSize).doSelectPageInfo(() -> httpMonitorService.list(spot, app, name, alarm, status));
        } else {
            value = httpMonitorService.list(spot, app, name, alarm, status);
        }
        return Result.builder().code(Result.SUCCESS).value(value).build();
    }

    @ApiOperation(value = "新增HTTP监控", produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping("add")
    public Result<?> add(@ApiParam(required = true) @RequestBody Map<String, Object> params) {
        TabHttpMonitor monitor = BeanUtil.mapToBean(MapUtil.get(params, "monitor", Map.class), TabHttpMonitor.class, true);
        TabAlarmRule rule = BeanUtil.mapToBean(MapUtil.get(params, "rule", Map.class), TabAlarmRule.class, true);
        Result result = httpMonitorService.add(monitor, rule);
        if (!result.isSuccess()) {
            return result;
        }
        httpHeartbeatTimer.add(monitor.getId());
        return Result.ok();
    }

    @ApiOperation(value = "修改HTTP监控", produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping("update")
    public Result<?> update(@ApiParam(required = true) @RequestBody Map<String, Object> params) {
        TabHttpMonitor monitor = BeanUtil.mapToBean(MapUtil.get(params, "monitor", Map.class), TabHttpMonitor.class, true);
        TabAlarmRule rule = BeanUtil.mapToBean(MapUtil.get(params, "rule", Map.class), TabAlarmRule.class, true);
        Result result = httpMonitorService.update(monitor, rule);
        if (!result.isSuccess()) {
            return result;
        }
        httpHeartbeatTimer.update(monitor.getId());
        return Result.ok();
    }

    @ApiOperation(value = "删除HTTP监控", produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping("remove")
    public Result<?> remove(@ApiParam(required = true) @RequestBody TabHttpMonitor monitor) {
        int ret = httpMonitorService.delete(monitor.getId());
        if (ret <= 0) {
            return Result.fail();
        }
        httpHeartbeatTimer.update(monitor.getId());
        return Result.ok();
    }
}
