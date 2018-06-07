package com.all580.monitor.service.impl;

import com.all580.monitor.entity.TabAlarmContacts;
import com.all580.monitor.entity.TabAlarmContactsRelation;
import com.all580.monitor.mapper.TabAlarmContactsMapper;
import com.all580.monitor.mapper.TabAlarmContactsRelationMapper;
import com.all580.monitor.service.AlarmContactsService;
import com.all580.monitor.service.BaseService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 联系人service
 *
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description:
 * @date 2018/5/16 13:03
 */
@Service
public class AlarmContactsServiceImpl extends BaseService<TabAlarmContacts> implements AlarmContactsService {
    @Resource
    private TabAlarmContactsMapper alarmContactsMapper;
    @Resource
    private TabAlarmContactsRelationMapper alarmContactsRelationMapper;

    @Override
    public List<TabAlarmContacts> listByGroup(int group) {
        return alarmContactsMapper.search(group, null);
    }

    @Override
    public List<TabAlarmContacts> search(Integer group, String keyword) {
        return alarmContactsMapper.search(group, keyword);
    }

    @Override
    public int delete(Object key) {
        int ret = super.delete(key);
        if (ret > 0) {
            alarmContactsRelationMapper.delete(new TabAlarmContactsRelation().setContactsId((Integer) key));
        }
        return ret;
    }
}
