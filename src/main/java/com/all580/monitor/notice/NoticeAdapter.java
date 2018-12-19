package com.all580.monitor.notice;

import com.all580.monitor.dto.Result;
import com.all580.monitor.entity.TabAlarmHistory;
import com.all580.monitor.entity.TabAlarmRule;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: 通知适配器接口
 * @date 2018/5/16 11:42
 */
public interface NoticeAdapter<T> {
    Object run(T target, TabAlarmRule rule, TabAlarmHistory history, Result<String> result) throws Exception;

    /**
     * 通知类型
     * @return
     */
    int type();
}
