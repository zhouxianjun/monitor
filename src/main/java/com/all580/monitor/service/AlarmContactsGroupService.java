package com.all580.monitor.service;

import com.all580.monitor.dto.Result;
import com.all580.monitor.entity.TabAlarmContactsGroup;

import java.util.List;

/**
 * 报警联系人组servcie接口
 * @author panyi on 18-5-25.
 */
public interface AlarmContactsGroupService extends IService<TabAlarmContactsGroup>{
    /**
     * 新增联系人组
     * @param group 组
     * @param contacts 联系人列表
     * @return
     */
    Result<?> add(TabAlarmContactsGroup group, List<Integer> contacts);

    /**
     * 修改联系人组
     * @param group 组
     * @param contacts 联系人列表
     * @return
     */
    Result<?> update(TabAlarmContactsGroup group, List<Integer> contacts);
}
