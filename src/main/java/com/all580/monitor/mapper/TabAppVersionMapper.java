package com.all580.monitor.mapper;

import com.all580.monitor.entity.TabAppVersion;
import com.all580.monitor.util.IMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface TabAppVersionMapper extends IMapper<TabAppVersion> {
    /**
     * 查询应用版本列表
     * @param spot 景区ID
     * @param app 应用ID
     * @param version 版本号
     * @return {tab_spot.name,tab_app.*,alert_count}
     */
    List<Map> search(@Param("spot") Integer spot, @Param("app") Integer app, @Param("version") String version);

    /**
     * 获取应用最后一个版本信息
     * @param app 应用ID
     * @return
     */
    Map<String, Object> last(@Param("app") int app);
}
