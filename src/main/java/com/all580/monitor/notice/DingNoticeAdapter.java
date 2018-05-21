package com.all580.monitor.notice;

import cn.hutool.core.map.MapUtil;
import cn.hutool.json.JSONUtil;
import com.all580.monitor.Constant;
import com.all580.monitor.entity.TabAlarmHistory;
import com.all580.monitor.entity.TabAlarmRule;
import org.apache.commons.codec.Charsets;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
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
    protected HttpRequestBase request(String url, TabAlarmRule rule, TabAlarmHistory history) {
        return new HttpPost(url);
    }

    @Override
    protected HttpEntity entity(TabAlarmRule rule, TabAlarmHistory history) throws Exception {
        return new StringEntity(JSONUtil.toJsonStr(MapUtil.builder()
                .put("msgtype", "text")
                .put("text", Collections.singletonMap("content", content(rule, history)))
                .put("at", Collections.singletonMap("isAtAll", true))
                .build()), Charsets.UTF_8);
    }
}
