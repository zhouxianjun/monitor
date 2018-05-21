package com.all580.monitor.service.impl;

import com.all580.monitor.entity.TabAlarmRule;
import com.all580.monitor.mapper.TabAlarmRuleMapper;
import com.all580.monitor.service.AlarmRuleService;
import com.all580.monitor.service.BaseService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description:
 * @date 2018/5/15 16:34
 */
@Service
public class AlarmRuleServiceImpl extends BaseService<TabAlarmRule> implements AlarmRuleService {
    @Resource
    private TabAlarmRuleMapper tabAlarmRuleMapper;

    @Override
    public List<?> list(Integer spot, Integer app, String name, Boolean alarm, Boolean status) {
        return tabAlarmRuleMapper.search(spot, app, name, alarm, status);
    }
}
