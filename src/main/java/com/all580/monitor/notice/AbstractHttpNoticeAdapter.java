package com.all580.monitor.notice;

import com.all580.monitor.entity.TabAlarmHistory;
import com.all580.monitor.entity.TabAlarmRule;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: Http抽象通知适配器
 * @date 2018/5/16 14:04
 */
public abstract class AbstractHttpNoticeAdapter extends AbstractNotice implements NoticeAdapter<String> {
    @Autowired
    protected OkHttpClient okHttpClient;
    protected String method() {
        return "GET";
    }
    protected RequestBody request(String url, TabAlarmRule rule, TabAlarmHistory history) {
        return null;
    }
    protected Map<String, String> headers(TabAlarmRule rule, TabAlarmHistory history) {
        return null;
    }

    @Override
    public Object run(String target, TabAlarmRule rule, TabAlarmHistory history) throws Exception {
        Request.Builder builder = new Request.Builder()
                .url(target)
                .method(method(), request(target, rule, history));
        Map<String, String> headers = headers(rule, history);
        if (headers != null) {
            headers.forEach(builder::addHeader);
        }

        try (Response response = okHttpClient.newCall(builder.build()).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                return response.body().string();
            }
            return response;
        }
    }
}
