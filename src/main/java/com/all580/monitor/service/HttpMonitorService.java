package com.all580.monitor.service;

import com.all580.monitor.dto.Result;
import com.all580.monitor.entity.TabAlarmRule;
import com.all580.monitor.entity.TabHttpMonitor;

import java.util.List;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: HTTP监控服务
 * @date 2018/5/22 9:08
 */
public interface HttpMonitorService extends IService<TabHttpMonitor> {
    /**
     * 获取列表
     * @param spot 景区ID
     * @param app 应用ID
     * @param name 站点名称
     * @param alarm 是否正在报警
     * @param status 状态
     * @return
     */
    List<?> list(Integer spot, Integer app, String name, Boolean alarm, Boolean status);

    /**
     * 新增
     * @param monitor 监控配置
     * @param rule 报警规则
     * @return
     */
    Result<?> add(TabHttpMonitor monitor, TabAlarmRule rule);

    /**
     * 修改
     * @param monitor 监控配置
     * @param rule 报警规则
     * @return
     */
    Result<?> update(TabHttpMonitor monitor, TabAlarmRule rule);
}
