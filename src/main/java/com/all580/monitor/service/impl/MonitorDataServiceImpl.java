package com.all580.monitor.service.impl;

import com.all580.monitor.entity.TabMonitorData;
import com.all580.monitor.service.BaseService;
import com.all580.monitor.service.MonitorDataService;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description:
 * @date 2018/5/16 9:41
 */
@Service
public class MonitorDataServiceImpl extends BaseService<TabMonitorData> implements MonitorDataService {
    @Override
    public List<TabMonitorData> selectByMetricAndResource(String metric, String resource, Date start, Date end) {
        Example example = new Example(TabMonitorData.class);
        Example.Criteria and = example.and();
        if (metric != null) {
            and.andEqualTo("metric", metric);
        }
        if (resource != null) {
            and.andEqualTo("resource", resource);
        }
        if (start != null) {
            and.andBetween("uploadTime", start, end == null ? new Date() : end);
        }
        return mapper.selectByExample(example);
    }
}
