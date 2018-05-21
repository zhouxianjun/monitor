package com.all580.monitor.controller;

import com.all580.monitor.dto.Result;
import com.all580.monitor.service.AlarmHistoryService;
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

import java.util.Date;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: 报警历史记录API
 * @date 2018/5/17 9:16
 */
@Api(tags = "报警历史记录接口")
@RestController
@CrossOrigin(allowCredentials = "true")
@RequestMapping("api/alarm/history")
public class AlarmHistoryController {
    @Autowired
    private AlarmHistoryService alarmHistoryService;

    @ApiOperation(value = "获取报警历史记录列表", notes = "如果传入page参数则返回value为PageInfo", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "spot", value = "景区ID"),
            @ApiImplicitParam(name = "app", value = "应用ID"),
            @ApiImplicitParam(name = "name", value = "规则名称(模糊匹配)"),
            @ApiImplicitParam(name = "status", value = "状态"),
            @ApiImplicitParam(name = "start", value = "开始时间"),
            @ApiImplicitParam(name = "end", value = "结束时间"),
            @ApiImplicitParam(name = "pageNum", value = "页总数"),
            @ApiImplicitParam(name = "pageSize", value = "页总数")
    })
    @GetMapping("list")
    public Result<?> list(Integer spot, Integer app, String name, Boolean status, Date start, Date end, Integer pageNum, Integer pageSize) {
        Object value;
        if (pageNum != null && pageSize != null) {
            value = PageHelper.startPage(pageNum, pageSize).doSelectPageInfo(() -> alarmHistoryService.list(spot, app, name, status, start, end));
        } else {
            value = alarmHistoryService.list(spot, app, name, status, start, end);
        }
        return Result.builder().code(Result.SUCCESS).value(value).build();
    }
}
