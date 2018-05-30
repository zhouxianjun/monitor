package com.all580.monitor.controller;

import cn.hutool.http.Method;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import com.all580.monitor.dto.Result;
import com.all580.monitor.manager.ESManager;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

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
public class LogController {
    @Autowired
    private ESManager esManager;

    @PostMapping("search")
    public Result<?> search(@RequestBody Map<String, Object> params) throws IOException {
        JSON json = esManager.execute("/logstash-*/_search", Method.POST, JSONUtil.toJsonStr(params));
        Object error = json.getByPath("error");
        return Result.builder().code(error == null ? Result.SUCCESS : Result.FAIL).msg(error != null ? error.toString() : null).value(json.toString()).build();
    }
}
