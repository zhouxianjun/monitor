package com.all580.monitor.service;

import com.all580.monitor.entity.TabAlarmContacts;

import java.util.List;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: 报警联系人服务
 * @date 2018/5/16 13:01
 */
public interface AlarmContactsService extends IService<TabAlarmContacts> {
    /**
     * 获取组内的联系人
     * @param group 组ID
     * @return
     */
    List<TabAlarmContacts> listByGroup(int group);
}
