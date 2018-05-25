package com.all580.monitor.mapper;

import com.all580.monitor.entity.TabAlarmRule;
import org.apache.ibatis.annotations.Param;
import com.all580.monitor.util.IMapper;

import java.util.List;
import java.util.Map;

public interface TabAlarmRuleMapper extends IMapper<TabAlarmRule> {
    /**
     * 搜索报警规则列表
     * @param spot 景区ID
     * @param app 应用ID
     * @param name 规则名称
     * @param alarm 是否正在报警
     * @param status 状态
     * @return {ts.name, ta.name, tah.status, tar.*}
     */
    List<Map> search(@Param("spot") Integer spot, @Param("app") Integer app, @Param("name") String name, @Param("alarm") Boolean alarm, @Param("status") Boolean status);
}