package com.all580.monitor.service;

import com.all580.monitor.entity.TabAlarmHistory;
import com.all580.monitor.entity.TabAlarmNotice;
import com.all580.monitor.entity.TabAlarmRule;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: 报警通知服务
 * @date 2018/5/16 11:54
 */
public interface AlarmNoticeService extends IService<TabAlarmNotice> {
    /**
     * 通知
     * @param type 类型
     * @param target 目标
     * @param history 报警最后一次历史
     * @param rule 报警规则
     * @return
     */
    Object notice(int type, Object target, TabAlarmRule rule, TabAlarmHistory history) throws Exception;
}
