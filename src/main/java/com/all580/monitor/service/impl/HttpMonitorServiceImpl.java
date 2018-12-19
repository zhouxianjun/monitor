package com.all580.monitor.service.impl;

import cn.hutool.core.util.StrUtil;
import com.all580.monitor.Constant;
import com.all580.monitor.dto.Result;
import com.all580.monitor.entity.TabAlarmRule;
import com.all580.monitor.entity.TabHttpMonitor;
import com.all580.monitor.express.QLExpressMgr;
import com.all580.monitor.mapper.TabHttpMonitorMapper;
import com.all580.monitor.service.AlarmRuleService;
import com.all580.monitor.service.BaseService;
import com.all580.monitor.service.HttpMonitorService;
import com.all580.monitor.task.HttpHeartbeatTimer;
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
 * @date 2018/5/22 9:08
 */
@Service
public class HttpMonitorServiceImpl extends BaseService<TabHttpMonitor> implements HttpMonitorService {
    @Resource
    private TabHttpMonitorMapper tabHttpMonitorMapper;
    @Autowired
    private AlarmRuleService alarmRuleService;
    @Autowired
    private HttpHeartbeatTimer httpHeartbeatTimer;
    @Autowired
    private QLExpressMgr qlExpressMgr;
    @Override
    public List<?> list(Integer spot, Integer app, String name, Boolean alarm, Boolean status) {
        return tabHttpMonitorMapper.search(spot, app, name, alarm != null && alarm ? true : null, status);
    }

    @Override
    public int save(TabHttpMonitor entity) {
        if (StrUtil.isNotBlank(entity.getScript()) && !qlExpressMgr.validate(entity.getScript())) {
            throw new RuntimeException("数据脚本错误");
        }
        return super.save(entity);
    }

    @Override
    public int updateNotNull(TabHttpMonitor entity) {
        if (StrUtil.isNotBlank(entity.getScript()) && !qlExpressMgr.validate(entity.getScript())) {
            throw new RuntimeException("数据脚本错误");
        }
        return super.updateNotNull(entity);
    }

    @Override
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public Result<?> add(TabHttpMonitor monitor, TabAlarmRule rule) {
        int ret = save(monitor);
        if (ret > 0) {
            rule.setResource(Constant.Metric.HTTP_HEARTBEAT + "." + monitor.getId())
                    .setStatus(true)
                    .setTimer(false)
                    .setCreateTime(new Date())
                    .setAppId(monitor.getAppId())
                    .setMetric(Constant.Metric.HTTP_HEARTBEAT)
                    .setName(monitor.getName() + "-HTTP心跳");
            ret = alarmRuleService.save(rule);
            if (ret <= 0) {
                throw new RuntimeException("新增HTTP监控失败: 新增报警规则失败");
            }
            monitor.setAlarmRuleId(rule.getId());
            ret = updateNotNull(monitor);
            if (ret <= 0) {
                throw new RuntimeException("新增HTTP监控失败: 更新报警规则ID失败");
            }
        }
        httpHeartbeatTimer.add(monitor.getId());
        return Result.ok();
    }

    @Override
    public Result<?> update(TabHttpMonitor monitor, TabAlarmRule rule) {
        int ret = updateNotNull(monitor);
        if (ret > 0) {
            rule.setId(monitor.getAlarmRuleId()).setAppId(monitor.getAppId()).setName(monitor.getName() + "-HTTP心跳").setTimer(false).setCurrentCount(null).setLastHistory(null);
            ret = alarmRuleService.updateNotNull(rule);
            if (ret <= 0) {
                throw new RuntimeException("修改HTTP监控失败: 修改报警规则失败");
            }
        }
        httpHeartbeatTimer.update(monitor.getId());
        return Result.ok();
    }

    @Override
    public int delete(Object key) {
        TabHttpMonitor monitor = selectByKey(key);
        if (monitor == null) {
            return 0;
        }
        int ret = super.delete(key);
        if (ret > 0 && monitor.getAlarmRuleId() != null) {
            ret = alarmRuleService.delete(monitor.getAlarmRuleId());
            if (ret <= 0) {
                throw new RuntimeException("删除HTTP监控失败: 删除报警规则失败");
            }
        }
        httpHeartbeatTimer.update(monitor.getId());
        return ret;
    }
}
