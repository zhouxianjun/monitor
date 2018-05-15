package com.all580.monitor.controller;

import com.all580.monitor.dto.Result;
import com.all580.monitor.service.SpotService;
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

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: 景区API
 * @date 2018/5/15 9:39
 */
@Api(tags = "景区接口")
@RestController
@CrossOrigin(allowCredentials = "true")
@RequestMapping("api/spot")
public class SpotController {
    @Autowired
    private SpotService spotService;

    /**
     * 获取景区列表
     * @return
     */
    @ApiOperation(value = "获取景区列表", notes = "如果传入page参数则返回value为PageInfo", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "页总数"),
            @ApiImplicitParam(name = "pageSize", value = "页总数")
    })
    @GetMapping("list")
    public Result<?> list(Integer pageNum, Integer pageSize) {
        Object value;
        if (pageNum != null && pageSize != null) {
            value = PageHelper.startPage(pageNum, pageSize).doSelectPageInfo(() -> spotService.all());
        } else {
            value = spotService.all();
        }
        return Result.builder().code(Result.SUCCESS).value(value).build();
    }
}
