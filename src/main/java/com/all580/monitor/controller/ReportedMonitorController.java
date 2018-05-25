package com.all580.monitor.controller;

import com.all580.monitor.dto.Result;
import com.all580.monitor.entity.TabHttpMonitor;
import com.all580.monitor.entity.TabReportedMonitor;
import com.all580.monitor.service.ReportedMonitorService;
import com.all580.monitor.vo.MonitorOperateVo;
import com.github.pagehelper.PageHelper;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: 上报监控API
 * @date 2018/5/22 14:16
 */
@Api(tags = "上报监控接口")
@RestController
@CrossOrigin(allowCredentials = "true")
@RequestMapping("api/monitor/reported")
public class ReportedMonitorController {
    @Autowired
    private ReportedMonitorService reportedMonitorService;

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
            value = PageHelper.startPage(pageNum, pageSize).doSelectPageInfo(() -> reportedMonitorService.list(spot, app, name, alarm, status));
        } else {
            value = reportedMonitorService.list(spot, app, name, alarm, status);
        }
        return Result.builder().code(Result.SUCCESS).value(value).build();
    }

    @ApiOperation(value = "新增上报监控", produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping("add")
    public Result<?> add(@ApiParam(required = true) @RequestBody MonitorOperateVo<TabReportedMonitor> vo) {
        return reportedMonitorService.add(vo.getMonitor(), vo.getRule());
    }

    @ApiOperation(value = "修改上报监控", produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping("update")
    public Result<?> update(@ApiParam(required = true) @RequestBody MonitorOperateVo<TabReportedMonitor> vo) {
        return reportedMonitorService.update(vo.getMonitor(), vo.getRule());
    }

    @ApiOperation(value = "删除HTTP监控", produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping("remove")
    public Result<?> remove(@ApiParam(required = true) @RequestBody TabHttpMonitor monitor) {
        int ret = reportedMonitorService.delete(monitor.getId());
        if (ret <= 0) {
            return Result.fail();
        }
        return Result.ok();
    }
}
