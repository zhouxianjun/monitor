package com.all580.monitor.service.impl;

import cn.hutool.core.util.StrUtil;
import com.all580.monitor.entity.TabApp;
import com.all580.monitor.mapper.TabAppMapper;
import com.all580.monitor.service.AppService;
import com.all580.monitor.service.BaseService;
import org.apache.commons.lang.BooleanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private final Map<Integer, Boolean> appAlarmStatus = new HashMap<>();

    @PostConstruct
    public void init() {
        // 初始化通知适配器
        all().forEach(app -> appAlarmStatus.put(app.getId(), app.getAlarm()));
    }

    @Override
    public boolean isAlarm(int appId) {
        return BooleanUtils.toBoolean(appAlarmStatus.get(appId));
    }

    @Override
    public List<?> list(Integer spot, String name, Boolean normal) {
        return tabAppMapper.search(spot, name, normal);
    }

    @Override
    public int updateNotNull(TabApp entity) {
        int ret = super.updateNotNull(entity);
        if (ret > 0) {
            appAlarmStatus.put(entity.getId(), entity.getAlarm());
        }
        return ret;
    }

    @Override
    public int updateAll(TabApp entity) {
        int ret = super.updateAll(entity);
        if (ret > 0) {
            appAlarmStatus.put(entity.getId(), entity.getAlarm());
        }
        return ret;
    }

    @Override
    public int delete(Object key) {
        int ret = super.delete(key);
        if (ret > 0) {
            appAlarmStatus.remove(key);
        }
        return ret;
    }

    @Override
    public int deleteByIds(String ids) {
        int ret = super.deleteByIds(ids);
        int[] idArray = StrUtil.splitToInt(ids, ",");
        if (ret > 0 && ret == idArray.length) {
            Arrays.stream(idArray).forEach(appAlarmStatus::remove);
        }
        return ret;
    }
}
