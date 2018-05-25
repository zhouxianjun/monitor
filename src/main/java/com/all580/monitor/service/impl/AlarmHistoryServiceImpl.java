package com.all580.monitor.service.impl;

import com.all580.monitor.entity.TabAlarmHistory;
import com.all580.monitor.mapper.TabAlarmHistoryMapper;
import com.all580.monitor.service.AlarmHistoryService;
import com.all580.monitor.service.BaseService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description:
 * @date 2018/5/16 8:33
 */
@Service
public class AlarmHistoryServiceImpl extends BaseService<TabAlarmHistory> implements AlarmHistoryService {
    @Resource
    private TabAlarmHistoryMapper tabAlarmHistoryMapper;

    @Override
    public List<?> list(Integer spot, Integer app, String name, Integer status, Date start, Date end) {
        return tabAlarmHistoryMapper.search(spot, app, name, status, start, end);
    }
}
