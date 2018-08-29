package com.all580.monitor.service.impl;

import com.all580.monitor.entity.TabAppVersion;
import com.all580.monitor.mapper.TabAppVersionMapper;
import com.all580.monitor.service.AppVersionService;
import com.all580.monitor.service.BaseService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description:
 * @date 2018/8/27 10:09
 */
@Service
public class AppVersionServiceImpl extends BaseService<TabAppVersion> implements AppVersionService {
    @Resource
    private TabAppVersionMapper appVersionMapper;

    @Override
    public List<?> list(Integer spot, Integer app, String version) {
        return appVersionMapper.search(spot, app, version);
    }
}
