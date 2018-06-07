package com.all580.monitor.notice;

import cn.hutool.core.date.DateUtil;
import com.all580.monitor.Constant;
import com.all580.monitor.entity.TabAlarmHistory;
import com.all580.monitor.entity.TabAlarmRule;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description:
 * @date 2018/5/16 14:25
 */
public abstract class AbstractNotice {
    protected String content(TabAlarmRule rule, TabAlarmHistory history) {
        switch (history.getStatus()) {
            case Constant.HistoryStatus.ALARM:
                return String.format("您监控%s,%s发生告警,请查看", rule.getName(), DateUtil.formatDateTime(history.getCreateTime()));
            case Constant.HistoryStatus.NORMAL:
                return String.format("您监控%s,%s已恢复正常", rule.getName(), DateUtil.formatDateTime(history.getCreateTime()));
            default:
                return String.format("您监控%s有情况", rule.getName());
        }
    }

    protected String title(TabAlarmRule rule, TabAlarmHistory history) {
        switch (history.getStatus()) {
            case Constant.HistoryStatus.ALARM:
                return "发生告警";
            case Constant.HistoryStatus.NORMAL:
                return "恢复正常";
            default:
                return "有情况";
        }
    }
}
