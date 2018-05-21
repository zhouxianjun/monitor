package com.all580.monitor.service;

import com.all580.monitor.entity.TabMonitorData;

import java.util.Date;
import java.util.List;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: 监控数据服务
 * @date 2018/5/16 9:39
 */
public interface MonitorDataService extends IService<TabMonitorData> {
    /**
     * 根据指标项与资源标识获取一段时间内的监控数据
     * @param metric 指标项
     * @param resource 资源标识
     * @param start 开始时间
     * @param end 结束时间
     * @return
     */
    List<TabMonitorData> selectByMetricAndResource(String metric, String resource, Date start, Date end);
}
