package com.all580.monitor.controller.business;

import com.all580.monitor.annotation.Business;
import com.all580.monitor.dto.Result;
import com.all580.monitor.entity.TFunc;
import com.all580.monitor.mapper.TFuncMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: 独立版的权限配置
 * @date 2018/7/2 15:18
 */
@Api(tags = "独立版的权限接口")
@RestController
@CrossOrigin(allowCredentials = "true")
@RequestMapping("api/business/standalone/auth")
@Business(types = 1)
public class StandaloneAuthController {
    @Resource
    private TFuncMapper funcMapper;

    @ApiOperation(value = "获取所有菜单列表", produces = MediaType.APPLICATION_JSON_VALUE)
    @GetMapping("list/{appId}")
    public Result<?> list(@ApiParam(required = true) @PathVariable int appId) {
        List<TFunc> funcs = funcMapper.selectAll();
        return Result.builder().code(Result.SUCCESS).value(funcs).build();
    }

    @ApiOperation(value = "新增菜单", produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping("add/{appId}")
    public Result<?> add(
            @ApiParam(required = true) @PathVariable int appId,
            @ApiParam(required = true) @RequestBody TFunc func) {
        func.setCreateTime(new Date());
        if (func.getPid() == null) {
            throw new NullPointerException("pid is not null");
        }
        int ret = funcMapper.insertSelective(func);
        return ret > 0 ? Result.ok() : Result.fail();
    }

    @ApiOperation(value = "修改菜单", produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping("update/{appId}")
    public Result<?> update(
            @ApiParam(required = true) @PathVariable int appId,
            @ApiParam(required = true) @RequestBody TFunc func) {
        int ret = funcMapper.updateByPrimaryKeySelective(func);
        return ret > 0 ? Result.ok() : Result.fail();
    }
}
