package com.all580.monitor.service;

import com.all580.monitor.entity.TabApp;

import java.util.List;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: 应用服务
 * @date 2018/5/11 9:36
 */
public interface AppService extends IService<TabApp> {
    /**
     * 获取引用list
     * @param spot 景区ID
     * @param name 应用名称
     * @param normal 是否正常
     * @return
     */
    List<?> list(Integer spot, String name, Boolean normal);
}
