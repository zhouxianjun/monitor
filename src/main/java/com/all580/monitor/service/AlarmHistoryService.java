package com.all580.monitor.service;

import com.all580.monitor.entity.TabAlarmHistory;

import java.util.Date;
import java.util.List;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: 报警历史服务
 * @date 2018/5/16 8:32
 */
public interface AlarmHistoryService extends IService<TabAlarmHistory> {
    /**
     * 获取列表
     * @param spot 景区ID
     * @param app 应用ID
     * @param name 规则名称
     * @param status 状态
     * @param start 开始时间
     * @param end 结束时间
     * @return
     */
    List<?> list(Integer spot, Integer app, String name, Integer status, Date start, Date end);
}
