package com.all580.monitor.task;

import cn.hutool.core.lang.Validator;
import com.all580.monitor.entity.TabAlarmRule;
import com.all580.monitor.entity.TabHttpMonitor;
import com.all580.monitor.service.AlarmRuleService;
import com.all580.monitor.service.HttpMonitorService;
import io.netty.util.HashedWheelTimer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: HTTP心跳任务
 * @date 2018/5/22 9:03
 */
@Component
@Slf4j
public class HttpHeartbeatTimer implements CommandLineRunner, ApplicationListener<ContextClosedEvent> {
    @Autowired
    private HttpMonitorService httpMonitorService;
    @Autowired
    private AlarmRuleService alarmRuleService;
    @Autowired
    private ApplicationContext applicationContext;
    private final Map<Integer, HttpHeartbeatTaskClearTrace> taskMap = new HashMap<>();
    private final HashedWheelTimer timer = new HashedWheelTimer();


    @Override
    public void run(String... args) throws Exception {
        Example example = new Example(TabHttpMonitor.class);
        example.and().andEqualTo("status", true);
        List<TabHttpMonitor> list = httpMonitorService.selectByExample(example);
        if (list != null) {
            list.forEach(this::add);
        }
    }

    public void add(int id) {
        add(httpMonitorService.selectByKey(id));
    }

    public void update(int id) {
        HttpHeartbeatTaskClearTrace task = taskMap.get(id);
        if (task != null) {
            task.stop(true);
            taskMap.remove(id);
        }
        this.add(id);
    }

    public void updateRule(int ruleId) {
        List<TabHttpMonitor> list = httpMonitorService.select(new TabHttpMonitor().setAlarmRuleId(ruleId));
        if (list != null) {
            list.forEach(m -> {
                if (taskMap.containsKey(m.getId())) {
                    taskMap.get(m.getId()).setRule(null);
                }
            });
        }
    }

    private void add(TabHttpMonitor monitor) {
        if (monitor == null || !monitor.getStatus() || !Validator.isUrl(monitor.getUrl())) {
            return;
        }
        TabAlarmRule rule = alarmRuleService.selectByKey(monitor.getAlarmRuleId());
        if (rule == null || rule.getInterval() <= 0) {
            return;
        }
        if (taskMap.containsKey(monitor.getId())) {
            log.warn("该HTTP监控已存在: {}", monitor);
            return;
        }
        HttpHeartbeatTaskClearTrace task = applicationContext.getBean(HttpHeartbeatTaskClearTrace.class);
        task.setMonitor(monitor);
        task.setRule(rule);
        timer.newTimeout(task, rule.getInterval(), TimeUnit.MINUTES);
        taskMap.put(monitor.getId(), task);
        log.info("添加HTTP监控任务: {}", monitor);
    }

    @Override
    public void onApplicationEvent(ContextClosedEvent contextClosedEvent) {
        log.info("Shutdown...");
        taskMap.values().forEach(task -> task.stop(true));
        timer.stop();
        log.info("Shutdown completed");
    }
}
