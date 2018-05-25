package com.all580.monitor.mapper;

import com.all580.monitor.entity.TabAlarmHistory;
import org.apache.ibatis.annotations.Param;
import com.all580.monitor.util.IMapper;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface TabAlarmHistoryMapper extends IMapper<TabAlarmHistory> {
    /**
     * 搜索报警历史列表
     * @param spot 景区ID
     * @param app 应用ID
     * @param name 规则名称
     * @param status 状态
     * @param start 开始时间
     * @param end 结束时间
     * @return {ts.name, ta.name, tah.status, tar.*}
     */
    List<Map> search(
            @Param("spot") Integer spot,
            @Param("app") Integer app,
            @Param("name") String name,
            @Param("status") Integer status,
            @Param("start") Date start,
            @Param("end") Date end);
}