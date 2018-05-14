package com.all580.monitor.controller;

import com.all580.monitor.dto.Result;
import com.all580.monitor.entity.TabApp;
import com.all580.monitor.service.AppService;
import com.github.pagehelper.PageHelper;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: 应用API
 * @date 2018/5/11 9:42
 */
@Api(tags = "应用接口")
@RestController
@CrossOrigin(allowCredentials = "true")
@RequestMapping("api/app")
public class AppController {
    @Autowired
    private AppService appService;

    /**
     * 获取应用列表
     * @param spot 景区ID
     * @return
     */
    @ApiOperation(value = "获取应用列表", notes = "如果传入page参数则返回value为PageInfo", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "spot", value = "景区ID"),
            @ApiImplicitParam(name = "name", value = "应用名称(模糊匹配)"),
            @ApiImplicitParam(name = "pageNum", value = "页总数"),
            @ApiImplicitParam(name = "pageSize", value = "页总数"),
    })
    @GetMapping("list")
    public Result<?> list(Integer spot, String name, Integer pageNum, Integer pageSize) {
        Object value;
        if (pageNum != null && pageSize != null) {
            value = PageHelper.startPage(pageNum, pageSize).doSelectPageInfo(() -> appService.list(spot, name));
        } else {
            value = appService.list(spot, name);
        }
        return Result.builder().code(Result.SUCCESS).value(value).build();
    }

    @ApiOperation(value = "新增应用", produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping("add")
    public Result<?> add(@ApiParam(required = true) @RequestBody TabApp app) {
        int ret = appService.save(app);
        if (ret <= 0) {
            return Result.fail();
        }
        return Result.ok();
    }

    @ApiOperation(value = "修改应用", produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping("update")
    public Result<?> update(@ApiParam(required = true) @RequestBody TabApp app) {
        int ret = appService.updateNotNull(app);
        if (ret <= 0) {
            return Result.fail();
        }
        return Result.ok();
    }
}
