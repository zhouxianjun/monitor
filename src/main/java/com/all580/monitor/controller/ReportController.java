package com.all580.monitor.controller;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.extra.servlet.ServletUtil;
import com.all580.monitor.dto.Result;
import com.all580.monitor.entity.TabMonitorData;
import com.all580.monitor.service.MonitorDataService;
import com.all580.monitor.vo.ReportMonitorDataVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: 上报数据API
 * @date 2018/5/24 16:18
 */
@Api(tags = "上报数据接口")
@RestController
@CrossOrigin(allowCredentials = "true")
@RequestMapping("api/report")
public class ReportController {
    @Autowired
    private MonitorDataService monitorDataService;
    @Autowired
    private Snowflake snowflake;

    @ApiOperation(value = "上报数据", produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping("push")
    public Result<?> push(@ApiParam(required = true, value = "传入JSON格式") @RequestBody ReportMonitorDataVo data) {
        String batch = StringUtils.defaultIfEmpty(data.getBatch(), String.valueOf(snowflake.nextId()));
        String host = StringUtils.defaultIfEmpty(data.getHost(), ServletUtil.getClientIP(((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest()));
        List<TabMonitorData> saves = data.getDimensions().stream()
                .map(d -> new TabMonitorData()
                        .setBatch(batch)
                        .setHost(host)
                        .setMetric(data.getMetric())
                        .setResource(data.getResource())
                        .setKey(d.getKey())
                        .setValue(String.valueOf(d.getValue()))
                        .setMemo(d.getMemo()))
                .collect(Collectors.toList());
        monitorDataService.save(saves, true);
        return Result.ok().value(batch);
    }
}
