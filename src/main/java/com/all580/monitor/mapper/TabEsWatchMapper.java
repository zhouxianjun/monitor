package com.all580.monitor.mapper;

import com.all580.monitor.entity.TabEsWatch;
import com.all580.monitor.util.IMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface TabEsWatchMapper extends IMapper<TabEsWatch> {
    /**
     * 搜索日志监控列表
     * @param spot 景区ID
     * @param app 应用ID
     * @param name 预警名称
     * @return {ts.name, ta.name, tew.*}
     */
    List<Map> search(@Param("spot") Integer spot, @Param("app") Integer app, @Param("name") String name, @Param("status") Boolean status);
}