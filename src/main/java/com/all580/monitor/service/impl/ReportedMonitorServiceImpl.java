package com.all580.monitor.service.impl;

import cn.hutool.db.DbRuntimeException;
import com.all580.monitor.Constant;
import com.all580.monitor.dto.Result;
import com.all580.monitor.entity.TabAlarmRule;
import com.all580.monitor.entity.TabReportedMonitor;
import com.all580.monitor.mapper.TabReportedMonitorMapper;
import com.all580.monitor.service.AlarmRuleService;
import com.all580.monitor.service.BaseService;
import com.all580.monitor.service.ReportedMonitorService;
import com.all580.monitor.task.AlarmRuleTimer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description:
 * @date 2018/5/25 11:15
 */
@Service
public class ReportedMonitorServiceImpl extends BaseService<TabReportedMonitor> implements ReportedMonitorService {
    @Resource
    private TabReportedMonitorMapper tabReportedMonitorMapper;
    @Autowired
    private AlarmRuleService alarmRuleService;
    @Autowired
    private AlarmRuleTimer alarmRuleTimer;

    @Override
    public List<?> list(Integer spot, Integer app, String name, Boolean alarm, Boolean status) {
        return tabReportedMonitorMapper.search(spot, app, name, alarm != null && alarm ? true : null, status);
    }

    @Override
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public Result<?> add(TabReportedMonitor monitor, TabAlarmRule rule) {
        int ret = save(monitor);
        if (ret > 0) {
            rule.setResource(Constant.Metric.REPORTED_HEARTBEAT + "." + monitor.getId())
                    .setStatus(true)
                    .setTimer(true)
                    .setCreateTime(new Date())
                    .setAppId(monitor.getAppId())
                    .setMetric(Constant.Metric.REPORTED_HEARTBEAT)
                    .setName(monitor.getName() + "-上报心跳");
            ret = alarmRuleService.save(rule);
            if (ret <= 0) {
                throw new DbRuntimeException("新增上报监控失败: 新增报警规则失败");
            }
            monitor.setAlarmRuleId(rule.getId());
            ret = updateNotNull(monitor);
            if (ret <= 0) {
                throw new DbRuntimeException("新增上报监控失败: 更新报警规则ID失败");
            }
        }
        alarmRuleTimer.add(rule.getId());
        return Result.ok();
    }

    @Override
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public Result<?> update(TabReportedMonitor monitor, TabAlarmRule rule) {
        int ret = updateNotNull(monitor);
        if (ret > 0) {
            rule.setId(monitor.getAlarmRuleId()).setAppId(monitor.getAppId()).setName(monitor.getName() + "-上报心跳").setTimer(true).setCurrentCount(null).setLastHistory(null);
            ret = alarmRuleService.updateNotNull(rule);
            if (ret <= 0) {
                throw new DbRuntimeException("修改上报监控失败: 修改报警规则失败");
            }
        }
        alarmRuleTimer.update(rule.getId());
        return Result.ok();
    }

    @Override
    public int delete(Object key) {
        TabReportedMonitor monitor = selectByKey(key);
        if (monitor == null) {
            return 0;
        }
        int ret = super.delete(key);
        if (ret > 0 && monitor.getAlarmRuleId() != null) {
            ret = alarmRuleService.delete(monitor.getAlarmRuleId());
            if (ret <= 0) {
                throw new DbRuntimeException("删除上报监控失败: 删除报警规则失败");
            }
        }
        alarmRuleTimer.update(monitor.getAlarmRuleId());
        return ret;
    }
}
