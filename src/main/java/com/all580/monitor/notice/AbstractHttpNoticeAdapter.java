package com.all580.monitor.notice;

import com.all580.monitor.entity.TabAlarmHistory;
import com.all580.monitor.entity.TabAlarmRule;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.util.Map;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: Http抽象通知适配器
 * @date 2018/5/16 14:04
 */
public abstract class AbstractHttpNoticeAdapter extends AbstractNotice implements NoticeAdapter<String> {
    protected HttpClient client() {
        return HttpClientBuilder.create().build();
    }
    protected HttpRequestBase request(String url, TabAlarmRule rule, TabAlarmHistory history) {
        return new HttpGet(url);
    }
    protected HttpEntity entity(TabAlarmRule rule, TabAlarmHistory history) throws Exception {
        return null;
    }
    protected Map<String, String> headers(TabAlarmRule rule, TabAlarmHistory history) {
        return null;
    }

    @Override
    public Object run(String target, TabAlarmRule rule, TabAlarmHistory history) throws Exception {
        HttpRequestBase request = request(target, rule, history);
        HttpEntity entity = entity(rule, history);
        if (entity != null && request instanceof HttpEntityEnclosingRequestBase) {
            ((HttpEntityEnclosingRequestBase) request).setEntity(entity);
        }Map<String, String> headers = headers(rule, history);
        if (headers != null) {
            headers.forEach(request::addHeader);
        }

        HttpResponse response = client().execute(request);
        return EntityUtils.toString(response.getEntity());
    }
}
