package com.all580.monitor.controller;

import cn.hutool.http.Method;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import com.all580.monitor.dto.Result;
import com.all580.monitor.entity.TabEsWatch;
import com.all580.monitor.entity.TabEsWatchJob;
import com.all580.monitor.entity.TabEsWatchLog;
import com.all580.monitor.manager.EsManager;
import com.all580.monitor.service.EsWatcherJobService;
import com.all580.monitor.service.EsWatcherLogService;
import com.all580.monitor.service.EsWatcherService;
import com.github.pagehelper.PageHelper;
import com.jayway.jsonpath.JsonPath;
import io.swagger.annotations.*;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: 日志API
 * @date 2018/5/28 11:41
 */
@Api(tags = "日志接口")
@RestController
@CrossOrigin(allowCredentials = "true")
@RequestMapping("api/log")
@Slf4j
public class LogController {
    @Autowired
    private EsManager esManager;
    @Autowired
    private EsWatcherService esWatcherService;
    @Autowired
    private EsWatcherJobService esWatcherJobService;
    @Autowired
    private EsWatcherLogService esWatcherLogService;

    @PostMapping("search")
    public Result<?> search(@RequestBody Map<String, Object> params) throws IOException {
        JSON json = esManager.execute("/logstash-*/_search", Method.POST, JSONUtil.toJsonStr(params));
        Object error = json.getByPath("error");
        return Result.builder().code(error == null ? Result.SUCCESS : Result.FAIL).msg(error != null ? error.toString() : null).value(json.toString()).build();
    }

    @PostMapping("watcher/hook")
    public Result<?> hook(@RequestParam String watcher, @RequestBody Map<String, Object> params) {
        List<String> traceIds = JsonPath.parse(params).read("$.hits.hits[*]._source.trace_id");
        TabEsWatchJob job = new TabEsWatchJob()
                .setCreateTime(new Date())
                .setStatus(false)
                .setWatcherId(watcher)
                .setTrace(traceIds.stream().distinct().collect(Collectors.joining(",")));
        esWatcherJobService.save(job);
        return Result.ok();
    }

    @ApiOperation(value = "获取日志监控列表", notes = "如果传入page参数则返回value为PageInfo", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "spot", value = "景区ID"),
            @ApiImplicitParam(name = "app", value = "应用ID"),
            @ApiImplicitParam(name = "name", value = "监控名称(模糊匹配)"),
            @ApiImplicitParam(name = "status", value = "状态"),
            @ApiImplicitParam(name = "pageNum", value = "页总数"),
            @ApiImplicitParam(name = "pageSize", value = "页总数")
    })
    @GetMapping("watcher/list")
    public Result<?> list(Integer spot, Integer app, String name, Boolean status, Integer pageNum, Integer pageSize) {
        Object value;
        if (pageNum != null && pageSize != null) {
            value = PageHelper.startPage(pageNum, pageSize).doSelectPageInfo(() -> esWatcherService.list(spot, app, name, status));
        } else {
            value = esWatcherService.list(spot, app, name, status);
        }
        return Result.builder().code(Result.SUCCESS).value(value).build();
    }

    @ApiOperation(value = "新增日志监控", produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping("watcher/add")
    public Result<?> add(@ApiParam(required = true) @RequestBody TabEsWatch watch) {
        int ret = esWatcherService.save(watch);
        return Result.builder().code(ret > 0 ? Result.SUCCESS : Result.FAIL).build();
    }

    @ApiOperation(value = "修改日志监控", produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping("watcher/update")
    public Result<?> update(@ApiParam(required = true) @RequestBody TabEsWatch watch) {
        int ret = esWatcherService.updateNotNull(watch);
        return Result.builder().code(ret > 0 ? Result.SUCCESS : Result.FAIL).build();
    }

    @ApiOperation(value = "删除日志监控", produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping("watcher/remove")
    public Result<?> remove(@ApiParam(required = true) @RequestBody TabEsWatch watch) {
        int ret = esWatcherService.delete(watch.getId());
        return Result.builder().code(ret > 0 ? Result.SUCCESS : Result.FAIL).build();
    }

    @ApiOperation(value = "获取日志监控的日志列表", notes = "如果传入page参数则返回value为PageInfo", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "watchId", value = "监控ID", required = true),
            @ApiImplicitParam(name = "traceId", value = "traceId"),
            @ApiImplicitParam(name = "pageNum", value = "页总数"),
            @ApiImplicitParam(name = "pageSize", value = "页总数")
    })
    @GetMapping("watcher/log/list")
    public Result<?> logList(@RequestParam Integer watchId, String traceId, Integer pageNum, Integer pageSize) {
        Object value;
        TabEsWatchLog entity = new TabEsWatchLog().setWatchId(watchId).setTraceId(StringUtils.isEmpty(traceId) ? null : traceId);
        if (pageNum != null && pageSize != null) {
            value = PageHelper.startPage(pageNum, pageSize).doSelectPageInfo(() -> esWatcherLogService.select(entity));
        } else {
            value = esWatcherLogService.select(entity);
        }
        return Result.builder().code(Result.SUCCESS).value(value).build();
    }
}
