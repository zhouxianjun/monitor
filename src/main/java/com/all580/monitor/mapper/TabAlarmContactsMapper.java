package com.all580.monitor.mapper;

import com.all580.monitor.entity.TabAlarmContacts;
import com.all580.monitor.util.IMapper;

import java.util.List;

public interface TabAlarmContactsMapper extends IMapper<TabAlarmContacts> {
    List<TabAlarmContacts> listByGroup(int groupId);
}