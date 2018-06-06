package com.all580.monitor.service;

import com.all580.monitor.entity.TabEsWatchLog;

import java.util.Date;
import java.util.List;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: ES 预警日志服务
 * @date 2018/6/1 9:19
 */
public interface EsWatcherLogService extends IService<TabEsWatchLog> {
    /**
     * 搜索并按照trace分组
     * @param watchId 监控ID
     * @param keyword 关键字
     * @return
     */
    List<TabEsWatchLog> searchGroupByTrace(int watchId, String keyword, Date start, Date end);
}
