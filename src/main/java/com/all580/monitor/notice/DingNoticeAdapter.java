package com.all580.monitor.notice;

import cn.hutool.core.map.MapUtil;
import cn.hutool.http.Method;
import cn.hutool.json.JSONUtil;
import com.all580.monitor.Constant;
import com.all580.monitor.dto.Result;
import com.all580.monitor.entity.TabAlarmHistory;
import com.all580.monitor.entity.TabAlarmRule;
import okhttp3.RequestBody;
import org.springframework.stereotype.Component;

import java.util.Collections;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: 钉钉机器人通知
 * @date 2018/5/16 14:53
 */
@Component
public class DingNoticeAdapter extends AbstractHttpNoticeAdapter {
    @Override
    public int type() {
        return Constant.NoticeType.DING;
    }

    @Override
    protected String method() {
        return Method.POST.name();
    }

    @Override
    protected RequestBody request(String url, TabAlarmRule rule, TabAlarmHistory history, Result<String> result) {
        return RequestBody.create(Constant.JSON, JSONUtil.toJsonStr(MapUtil.builder()
                .put("msgtype", "text")
                .put("text", Collections.singletonMap("content", content(rule, history, result)))
                .put("at", Collections.singletonMap("isAtAll", true))
                .build()));
    }
}
