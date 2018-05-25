package com.all580.monitor.mapper;

import com.all580.monitor.entity.TabHttpMonitor;
import org.apache.ibatis.annotations.Param;
import com.all580.monitor.util.IMapper;

import java.util.List;
import java.util.Map;

public interface TabHttpMonitorMapper extends IMapper<TabHttpMonitor> {
    /**
     * 搜索HTTP监控列表
     * @param spot 景区ID
     * @param app 应用ID
     * @param name 站点名称
     * @param alarm 是否正在报警
     * @return {ts.name, ta.name, tah.status, thm.*}
     */
    List<Map> search(@Param("spot") Integer spot, @Param("app") Integer app, @Param("name") String name, @Param("alarm") Boolean alarm, @Param("status") Boolean status);
}