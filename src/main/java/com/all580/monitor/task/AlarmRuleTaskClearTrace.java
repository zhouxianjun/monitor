package com.all580.monitor.task;

import com.all580.monitor.entity.TabAlarmHistory;
import com.all580.monitor.entity.TabAlarmRule;
import com.all580.monitor.entity.TabMonitorData;
import com.all580.monitor.manager.AlarmRuleManager;
import com.all580.monitor.service.AlarmHistoryService;
import com.all580.monitor.service.MonitorDataService;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: 报警规则任务
 * @date 2018/5/15 16:51
 */
@Component
@Scope("prototype")
@Slf4j
public class AlarmRuleTaskClearTrace implements TimerTask {
    @Setter
    private TabAlarmRule rule;
    private Timeout timeout;
    private boolean run = true;
    private TabAlarmHistory history;
    @Autowired
    private AlarmHistoryService alarmHistoryService;
    @Autowired
    private MonitorDataService monitorDataService;
    @Autowired
    private AlarmRuleManager alarmRuleManager;

    @Override
    public void run(Timeout timeout) throws Exception {
        log.debug("---开始报警规则任务: {}---", rule);
        try {
            Date curDate = new Date();
            getHistory();
            // 当前-间隔时间
            Date start = DateUtils.addMinutes(curDate, -rule.getInterval());
            // 获取本次检测区间的监控数据
            List<TabMonitorData> data = monitorDataService.selectByMetricAndResource(rule.getMetric(), rule.getResource(), start, null);
            this.history = alarmRuleManager.judge(data, rule, history, curDate);
        } catch (Throwable e) {
            log.warn("报警规则任务: {} 执行异常", rule, e);
        } finally {
            log.debug("---结束报警任务:{} - {}---", rule, history);
            if (this.run) {
                this.timeout = timeout.timer().newTimeout(this, rule.getInterval(), TimeUnit.MINUTES);
            }
        }
    }

    public void stop() {
        this.stop(false);
    }

    public void stop(boolean force) {
        this.run = false;
        if (force && timeout != null) {
            timeout.cancel();
        }
    }

    private TabAlarmHistory getHistory() {
        if (history == null && rule.getLastHistory() != null) {
            history = alarmHistoryService.selectByKey(rule.getLastHistory());
        }
        return history;
    }
}
