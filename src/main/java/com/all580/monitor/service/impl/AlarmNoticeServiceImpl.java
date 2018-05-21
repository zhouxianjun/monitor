package com.all580.monitor.service.impl;

import com.all580.monitor.entity.TabAlarmHistory;
import com.all580.monitor.entity.TabAlarmNotice;
import com.all580.monitor.entity.TabAlarmRule;
import com.all580.monitor.notice.NoticeAdapter;
import com.all580.monitor.service.AlarmNoticeService;
import com.all580.monitor.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description:
 * @date 2018/5/16 11:56
 */
@Service
public class AlarmNoticeServiceImpl extends BaseService<TabAlarmNotice> implements AlarmNoticeService {
    @Autowired
    private ApplicationContext applicationContext;
    private final Map<Integer, NoticeAdapter> adapterMap = new HashMap<>();

    @PostConstruct
    public void init() {
        // 初始化通知适配器
        applicationContext.getBeansOfType(NoticeAdapter.class).values().forEach(adapter -> adapterMap.put(adapter.type(), adapter));
    }

    @Override
    @SuppressWarnings("unchecked")
    @Retryable(value = Exception.class, backoff = @Backoff(delay = 2000, multiplier = 2))
    public Object notice(int type, Object target, TabAlarmRule rule, TabAlarmHistory history) throws Exception {
        if (adapterMap.containsKey(type)) {
            return adapterMap.get(type).run(target, rule, history);
        }
        return null;
    }
}
