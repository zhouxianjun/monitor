package com.all580.monitor.mapper;

import com.all580.monitor.entity.TabApp;
import org.apache.ibatis.annotations.Param;
import com.all580.monitor.util.IMapper;

import java.util.List;
import java.util.Map;

public interface TabAppMapper extends IMapper<TabApp> {
    /**
     * 查询应用列表
     * @param spot 景区ID
     * @param name 应用名称
     * @param normal 是否正常（当前不在报警）
     * @return {tab_spot.name,tab_app.*,alert_count}
     */
    List<Map> search(@Param("spot") Integer spot, @Param("name") String name, @Param("normal") Boolean normal);
}