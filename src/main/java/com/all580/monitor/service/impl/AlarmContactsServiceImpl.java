package com.all580.monitor.service.impl;

import com.all580.monitor.entity.TabAlarmContacts;
import com.all580.monitor.service.AlarmContactsService;
import com.all580.monitor.service.BaseService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description:
 * @date 2018/5/16 13:03
 */
@Service
public class AlarmContactsServiceImpl extends BaseService<TabAlarmContacts> implements AlarmContactsService {
    @Override
    public List<TabAlarmContacts> listByGroup(int group) {
        return null;
    }
}
