package com.all580.monitor.task;

import com.all580.monitor.entity.TabAlarmRule;
import com.all580.monitor.service.AlarmRuleService;
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
 * @Description: 初始化报警规则扫描任务
 * @date 2018/5/15 16:31
 */
@Component
@Slf4j
public class AlarmRuleTimer implements CommandLineRunner, ApplicationListener<ContextClosedEvent> {
    @Autowired
    private AlarmRuleService alarmRuleService;
    @Autowired
    private ApplicationContext applicationContext;
    private final Map<Integer, AlarmRuleTaskClearTrace> taskMap = new HashMap<>();
    private final HashedWheelTimer timer = new HashedWheelTimer();

    @Override
    public void run(String... args) throws Exception {
        Example example = new Example(TabAlarmRule.class);
        example.and()
                .andEqualTo("status", true)
                .andGreaterThan("interval", 0)
                .andEqualTo("timer", true);
        List<TabAlarmRule> list = alarmRuleService.selectByExample(example);
        if (list != null) {
            list.forEach(this::add);
        }
    }

    public void add(int id) {
        add(alarmRuleService.selectByKey(id));
    }

    public void update(Integer id) {
        if (id == null) {
            return;
        }
        AlarmRuleTaskClearTrace task = taskMap.get(id);
        if (task != null) {
            task.stop(true);
            taskMap.remove(id);
        }
        this.add(id);
    }

    private void add(TabAlarmRule rule) {
        if (rule == null || !rule.getStatus() || rule.getInterval() <= 0 || !rule.getTimer()) {
            return;
        }
        if (taskMap.containsKey(rule.getId())) {
            log.warn("该任务规则已存在: {}", rule);
            return;
        }
        AlarmRuleTaskClearTrace task = applicationContext.getBean(AlarmRuleTaskClearTrace.class);
        task.setRule(rule);
        timer.newTimeout(task, rule.getInterval(), TimeUnit.MINUTES);
        taskMap.put(rule.getId(), task);
        log.info("添加报警规则任务: {}", rule);
    }

    @Override
    public void onApplicationEvent(ContextClosedEvent contextClosedEvent) {
        log.info("Shutdown...");
        taskMap.values().forEach(task -> task.stop(true));
        timer.stop();
        log.info("Shutdown completed");
    }
}
