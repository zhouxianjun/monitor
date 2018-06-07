package com.all580.monitor.mapper;

import com.all580.monitor.entity.TabAlarmContacts;
import com.all580.monitor.util.IMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TabAlarmContactsMapper extends IMapper<TabAlarmContacts> {
    List<TabAlarmContacts> search(@Param("groupId") Integer groupId, @Param("keyword") String keyword);
}