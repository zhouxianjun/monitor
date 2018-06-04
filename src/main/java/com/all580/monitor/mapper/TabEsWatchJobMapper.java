package com.all580.monitor.mapper;

import com.all580.monitor.entity.TabEsWatchJob;
import com.all580.monitor.util.IMapper;

import java.util.List;

public interface TabEsWatchJobMapper extends IMapper<TabEsWatchJob> {
    /**
     * 查询未执行的trace任务
     * @param traces
     * @return
     */
    TabEsWatchJob selectByTraceAndUndone(List<String> traces);
}