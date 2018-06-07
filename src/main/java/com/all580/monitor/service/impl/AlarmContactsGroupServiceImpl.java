package com.all580.monitor.service.impl;

import cn.hutool.db.DbRuntimeException;
import com.all580.monitor.dto.Result;
import com.all580.monitor.entity.TabAlarmContactsGroup;
import com.all580.monitor.entity.TabAlarmContactsRelation;
import com.all580.monitor.mapper.TabAlarmContactsRelationMapper;
import com.all580.monitor.service.AlarmContactsGroupService;
import com.all580.monitor.service.BaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author panyi on 18-5-25.
 */
@Service
public class AlarmContactsGroupServiceImpl extends BaseService<TabAlarmContactsGroup> implements AlarmContactsGroupService {
    @Resource
    private TabAlarmContactsRelationMapper alarmContactsRelationMapper;

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public Result<?> add(TabAlarmContactsGroup group, List<Integer> contacts) {
        int ret = save(group);
        if (ret > 0) {
            saveRelation(group, contacts);
        }
        return Result.ok();
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public Result<?> update(TabAlarmContactsGroup group, List<Integer> contacts) {
        updateNotNull(group);
        alarmContactsRelationMapper.delete(new TabAlarmContactsRelation().setGroupId(group.getId()));
        saveRelation(group, contacts);
        return Result.ok();
    }

    @Override
    public int delete(Object key) {
        int ret = super.delete(key);
        if (ret > 0) {
            alarmContactsRelationMapper.delete(new TabAlarmContactsRelation().setGroupId((Integer) key));
        }
        return ret;
    }

    private void saveRelation(TabAlarmContactsGroup group, List<Integer> contacts) {
        int ret = alarmContactsRelationMapper.insertList(contacts.stream()
                .map(id -> new TabAlarmContactsRelation().setContactsId(id).setGroupId(group.getId()))
                .collect(Collectors.toList()));
        if (ret != contacts.size()) {
            throw new DbRuntimeException("插入联系人关联表异常");
        }
    }
}
