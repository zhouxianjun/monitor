package com.all580.monitor.controller;

import com.all580.monitor.dto.Result;
import com.all580.monitor.entity.TabAppVersion;
import com.all580.monitor.entity.TabResources;
import com.all580.monitor.service.AppService;
import com.all580.monitor.service.AppVersionService;
import com.all580.monitor.service.ResourceService;
import com.github.pagehelper.PageHelper;
import io.swagger.annotations.*;
import lombok.SneakyThrows;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tk.mybatis.mapper.entity.Example;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Optional;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: 应用版本API
 * @date 2018/5/11 9:42
 */
@Api(tags = "应用版本接口")
@RestController
@CrossOrigin(allowCredentials = "true")
@RequestMapping("api/app/version")
public class AppVersionController {
    @Autowired
    private AppVersionService appVersionService;
    @Autowired
    private ResourceService resourceService;
    @Autowired
    private AppService appService;
    @Value("${upload.path:/home/monitor/upload}")
    private String uploadPath;

    /**
     * 获取应用版本列表
     * @return
     */
    @ApiOperation(value = "获取应用版本列表", notes = "如果传入page参数则返回value为PageInfo", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "spot", value = "景区ID"),
            @ApiImplicitParam(name = "app", value = "应用ID"),
            @ApiImplicitParam(name = "version", value = "版本号(模糊匹配%)"),
            @ApiImplicitParam(name = "pageNum", value = "页总数"),
            @ApiImplicitParam(name = "pageSize", value = "页总数")
    })
    @GetMapping("list")
    public Result<?> list(Integer spot, Integer app, String version, Integer pageNum, Integer pageSize) {
        Object value;
        if (pageNum != null && pageSize != null) {
            value = PageHelper.startPage(pageNum, pageSize).doSelectPageInfo(() -> appVersionService.list(spot, app, version));
        } else {
            value = appVersionService.list(spot, app, version);
        }
        return Result.builder().code(Result.SUCCESS).value(value).build();
    }

    @ApiOperation(value = "获取应用最后一个版本", produces = MediaType.APPLICATION_JSON_VALUE)
    @GetMapping("last/{id}")
    public Result<?> last(@ApiParam(required = true) @PathVariable int id) {
        Example example = new Example(TabAppVersion.class);
        example.and().andEqualTo("id", id);
        example.orderBy("id").desc();
        return Result.builder().code(Result.SUCCESS).value(appVersionService.selectOneByExample(example)).build();
    }

    @ApiOperation(value = "新增应用版本")
    @PostMapping("add")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "appId", value = "应用ID", required = true),
            @ApiImplicitParam(name = "version", value = "版本号", required = true)
    })
    @SneakyThrows
    public Result<?> add(@RequestParam int appId, @RequestParam String version, @ApiParam(required = true) @RequestParam MultipartFile file) {
        Assert.notNull(appService.selectByKey(appId), "应用不存在");
        String sha1 = DigestUtils.sha1Hex(file.getInputStream());
        String fileName = Optional.ofNullable(file.getOriginalFilename()).orElse(Optional.ofNullable(file.getName()).orElse(sha1));
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
        TabResources resources = resourceService.selectOne(new TabResources().setMd5(sha1));
        Date date = new Date();
        if (resources == null) {
            String name = sha1 + (StringUtils.isBlank(suffix) ? "" : "." + suffix);
            Path path = Paths.get(uploadPath);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
            File saveFile = new File(path.toFile().getAbsoluteFile(), name);
            file.transferTo(saveFile);
            resources = new TabResources()
                    .setMd5(sha1)
                    .setName(fileName)
                    .setPath(saveFile.getAbsolutePath())
                    .setSize((int) file.getSize())
                    .setSuffix(suffix)
                    .setCreateTime(date);
            resourceService.save(resources);
        }

        int ret = appVersionService.save(
                new TabAppVersion()
                        .setAppId(appId)
                        .setResourceId(resources.getId())
                        .setVersion(version)
                        .setCreateTime(date)
        );
        return ret <= 0 ? Result.fail() : Result.ok();
    }

    @ApiOperation(value = "删除应用版本", produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping("remove/{id}")
    public Result<?> remove(@ApiParam(required = true) @PathVariable int id) {
        int ret = appVersionService.delete(id);
        return ret <= 0 ? Result.fail() : Result.ok();
    }
}
