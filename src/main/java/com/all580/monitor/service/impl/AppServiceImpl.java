package com.all580.monitor.service.impl;

import com.all580.monitor.entity.TabApp;
import com.all580.monitor.mapper.TabAppMapper;
import com.all580.monitor.service.AppService;
import com.all580.monitor.service.BaseService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description:
 * @date 2018/5/11 9:40
 */
@Service
public class AppServiceImpl extends BaseService<TabApp> implements AppService {
    @Resource
    private TabAppMapper tabAppMapper;

    @Override
    public List<?> list(Integer spot, String name, Boolean normal) {
        return tabAppMapper.search(spot, name, normal);
    }
}
