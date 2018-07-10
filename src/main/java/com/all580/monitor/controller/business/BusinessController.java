package com.all580.monitor.controller.business;

import cn.hutool.json.JSONUtil;
import com.all580.monitor.annotation.Business;
import com.all580.monitor.dto.Result;
import com.all580.monitor.entity.TabApp;
import com.all580.monitor.manager.BusinessManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: 应用业务服务
 * @date 2018/7/2 9:15
 */
@Api(tags = "应用业务接口")
@RestController
@CrossOrigin(allowCredentials = "true")
@RequestMapping("api/business")
@Business
public class BusinessController {
    @Autowired
    private BusinessManager businessManager;

    @ApiOperation(value = "获取应用业务列表", produces = MediaType.APPLICATION_JSON_VALUE)
    @GetMapping("list")
    public Result<?> list(Integer type) {
        Map<String, Collection<Integer>> map = businessManager.getBusinessMap().asMap();
        if (type == null) {
            return Result.builder().code(Result.SUCCESS).value(map).build();
        }
        return Result.builder().code(Result.SUCCESS).value(map
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue().contains(type))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
        ).build();
    }

    @ApiOperation(value = "代理请求")
    @RequestMapping("proxy")
    @SneakyThrows
    public Result<?> proxy(
            @ApiParam @RequestParam int appId,
            @ApiParam(hidden = true, readOnly = true) TabApp app,
            @ApiParam @RequestParam String url,
            @ApiParam @RequestParam String method,
            @ApiParam @RequestParam(required = false) String params,
            @ApiParam @RequestBody(required = false) Map body,
            HttpServletRequest request) {
        String m = request.getMethod();
        if (m.equalsIgnoreCase("POST")) {
            params = JSONUtil.toJsonStr(body);
        }
        return businessManager.proxy(app, url, method, params);
    }
}
