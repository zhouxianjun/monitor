package com.all580.monitor.service;

import com.all580.monitor.entity.TabAlarmRule;

import java.util.List;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: 报警规则服务
 * @date 2018/5/15 16:33
 */
public interface AlarmRuleService extends IService<TabAlarmRule> {
    /**
     * 获取列表
     * @param spot 景区ID
     * @param app 应用ID
     * @param name 规则名称
     * @param alarm 是否正在报警
     * @param status 状态
     * @return
     */
    List<?> list(Integer spot, Integer app, String name, Boolean alarm, Boolean status);
}
