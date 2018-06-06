package com.all580.monitor.mapper;

import com.all580.monitor.entity.TabEsWatchLog;
import com.all580.monitor.util.IMapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface TabEsWatchLogMapper extends IMapper<TabEsWatchLog> {
    List<TabEsWatchLog> searchGroupByTrace(@Param("watchId") int watchId, @Param("keyword") String keyword, @Param("start") Date start, @Param("end") Date end);
}