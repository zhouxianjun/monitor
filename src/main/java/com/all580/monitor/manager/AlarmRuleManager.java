package com.all580.monitor.manager;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.json.JSONUtil;
import com.all580.monitor.Constant;
import com.all580.monitor.annotation.NoticeType;
import com.all580.monitor.entity.*;
import com.all580.monitor.express.QLExpressMgr;
import com.all580.monitor.service.AlarmContactsService;
import com.all580.monitor.service.AlarmHistoryService;
import com.all580.monitor.service.AlarmNoticeService;
import com.all580.monitor.service.AlarmRuleService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: 报警规则管理
 * @date 2018/5/17 11:39
 */
@Component
@Slf4j
public class AlarmRuleManager {
    @Autowired
    private AlarmHistoryService alarmHistoryService;
    @Autowired
    private AlarmRuleService alarmRuleService;
    @Autowired
    private AlarmNoticeService alarmNoticeService;
    @Autowired
    private AlarmContactsService alarmContactsService;
    @Autowired
    private QLExpressMgr qlExpressMgr;

    /**
     * 判定
     * @param data 数据
     * @param rule 规则
     * @throws Throwable
     */
    public TabAlarmHistory judge(List<TabMonitorData> data, TabAlarmRule rule) throws Throwable {
        return judge(data, rule, rule.getLastHistory() != null ? alarmHistoryService.selectByKey(rule.getLastHistory()) : null, new Date());
    }

    /**
     * 判定
     * @param data 数据
     * @param rule 规则
     * @param history 历史
     * @param curDate 当前时间
     * @throws Throwable
     */
    public TabAlarmHistory judge(List<TabMonitorData> data, TabAlarmRule rule, TabAlarmHistory history, Date curDate) throws Throwable {
        if (CollectionUtils.isEmpty(data) && rule.getNodata()) {
            return process(rule, history, false, curDate);
        }
        // 执行脚本
        if (StringUtils.isNotEmpty(rule.getScript())) {
            Map context = MapUtil.builder().put("rule", rule).put("history", history).put("data", data).build();
            Object result = qlExpressMgr.execute(rule.getScript(), context);
            if (result instanceof Boolean && !(Boolean) result) {
                return process(rule, history, false, curDate);
            }
        }
        return process(rule, history, true, curDate);
    }

    private TabAlarmHistory process(TabAlarmRule rule, TabAlarmHistory history, boolean success, Date curDate) {
        if (success) {
            rule.setCurrentCount(0);
            if (history == null || history.getStatus() == Constant.HistoryStatus.NORMAL) {
                alarmRuleService.updateNotNull(rule);
                return history;
            }
            history = new TabAlarmHistory()
                    .setRuleId(rule.getId())
                    .setStatus(Constant.HistoryStatus.NORMAL)
                    .setDuration((int) (history.getDuration() + DateUtil.between(history.getCreateTime(), curDate, DateUnit.MINUTE)))
                    .setCreateTime(curDate);
        } else {
            // 自增当前次数
            rule.setCurrentCount(rule.getCurrentCount() + 1);
            // 判断是否达到错误次数
            if (rule.getCount() > rule.getCurrentCount()) {
                alarmRuleService.updateNotNull(rule);
                return history;
            }
            // 判断是否通道沉默
            if (history != null && history.getStatus() == Constant.HistoryStatus.SILENCE) {
                Date silenceDate = DateUtils.addMinutes(history.getCreateTime(), rule.getSilenceInterval() - rule.getInterval());
                if (silenceDate.after(curDate)) {
                    log.debug("报警规则任务通道沉默跳过处理: {}", rule);
                    return history;
                }
            }
            boolean isSilence = history != null && history.getStatus() == Constant.HistoryStatus.ALARM && rule.getSilenceInterval() > 0;
            history = new TabAlarmHistory()
                    .setRuleId(rule.getId())
                    .setStatus(isSilence ? Constant.HistoryStatus.SILENCE : Constant.HistoryStatus.ALARM)
                    .setDuration(Math.max(rule.getInterval() * rule.getCurrentCount(), rule.getInterval()))
                    .setCreateTime(curDate);
        }
        alarmHistoryService.save(history);
        rule.setLastHistory(history.getId());
        alarmRuleService.updateNotNull(rule);
        TabAlarmHistory finalHistory = history;
        ThreadUtil.execute(() -> this.notice(rule, finalHistory, curDate));
        return history;
    }

    private void notice(TabAlarmRule rule, TabAlarmHistory history, Date curDate) {
        log.debug("报警规则任务通知: {}", rule);
        try {
            if (StringUtils.isNotEmpty(rule.getAlarmCallback())) {
                doNotice(Constant.NoticeType.URL, rule.getAlarmCallback(), rule, history, curDate);
            }
            if (rule.getAlarmGroupId() != null) {
                List<TabAlarmContacts> contacts = alarmContactsService.listByGroup(rule.getAlarmGroupId());
                if (!CollectionUtils.isEmpty(contacts)) {
                    contacts.forEach(item -> this.doNotice(item, rule, history, curDate));
                }
            }
        } catch (Exception e) {
            log.warn("报警规则任务通知未知异常", e);
        }
    }

    private void doNotice(TabAlarmContacts contacts, TabAlarmRule rule, TabAlarmHistory history, Date curDate) {
        Field[] fields = ReflectUtil.getFields(TabAlarmContacts.class);
        for (Field field : fields) {
            NoticeType noticeType = field.getAnnotation(NoticeType.class);
            Object value = ReflectUtil.getFieldValue(contacts, field);
            if (noticeType != null && value != null) {
                doNotice(noticeType.value(), value, rule, history, curDate);
            }
        }
    }

    private void doNotice(int type, Object target, TabAlarmRule rule, TabAlarmHistory history, Date curDate) {
        TabAlarmNotice notice = new TabAlarmNotice()
                .setAlarmHistoryId(history.getId())
                .setType(type)
                .setStatus(false)
                .setCreateTime(curDate);
        alarmNoticeService.save(notice);
        try {
            Object result = alarmNoticeService.notice(type, target, rule, history);
            if (result == null || result.getClass().isPrimitive()) {
                notice.setResponse(String.valueOf(result));
            } else {
                notice.setResponse(JSONUtil.toJsonStr(result));
            }
            notice.setUpdateTime(new Date());
            notice.setStatus(true);
        } catch (Exception e) {
            notice.setResponse(ExceptionUtils.getStackTrace(e));
        }
        alarmNoticeService.updateNotNull(notice);
    }
}
