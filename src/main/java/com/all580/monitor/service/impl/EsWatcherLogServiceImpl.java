package com.all580.monitor.service.impl;

import com.all580.monitor.entity.TabEsWatchLog;
import com.all580.monitor.mapper.TabEsWatchLogMapper;
import com.all580.monitor.service.BaseService;
import com.all580.monitor.service.EsWatcherLogService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description:
 * @date 2018/6/1 9:20
 */
@Service
public class EsWatcherLogServiceImpl extends BaseService<TabEsWatchLog> implements EsWatcherLogService {
    @Resource
    private TabEsWatchLogMapper tabEsWatchLogMapper;

    @Override
    public List<TabEsWatchLog> searchGroupByTrace(int watchId, String keyword, Date start, Date end) {
        return tabEsWatchLogMapper.searchGroupByTrace(watchId, keyword, start, end);
    }
}
