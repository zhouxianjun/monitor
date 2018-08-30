package com.all580.monitor.service;

import com.all580.monitor.entity.TabAppVersion;

import java.util.List;
import java.util.Map;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description:
 * @date 2018/8/27 10:05
 */
public interface AppVersionService extends IService<TabAppVersion> {
    /**
     * 获取应用版本list
     * @param spot 景区ID
     * @param app 应用ID
     * @param version 版本号
     * @return
     */
    List<?> list(Integer spot, Integer app, String version);

    /**
     * 获取应用最后一个版本信息
     * @param app 应用ID
     * @return
     */
    Map<String, Object> last(int app);
}
