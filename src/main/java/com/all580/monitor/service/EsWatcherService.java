package com.all580.monitor.service;

import com.all580.monitor.entity.TabEsWatch;

import java.util.List;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: ES预警服务
 * @date 2018/5/31 11:09
 */
public interface EsWatcherService extends IService<TabEsWatch> {
    /**
     * 获取列表
     * @param spot 景区ID
     * @param app 应用ID
     * @param name 预警名称
     * @param status 状态
     * @return
     */
    List<?> list(Integer spot, Integer app, String name, Boolean status);
}
