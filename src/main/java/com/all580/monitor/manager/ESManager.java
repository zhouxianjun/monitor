package com.all580.monitor.manager;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.Method;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import com.all580.monitor.Constant;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.apache.commons.codec.Charsets;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: elasticsearch
 * @date 2018/5/28 11:44
 */
@Component
public class ESManager {
    @Autowired
    private OkHttpClient okHttpClient;
    @Value("${es.api.url}")
    private String esApiUrl;
    @Value("${es.api.user}")
    private String user;
    @Value("${es.api.password}")
    private String password;
    public static final String TRACE_AGGS = "{\"groupByTrace\":{\"terms\":{\"field\":\"trace_id.keyword\"},\"aggs\":{\"info\":{\"top_hits\":{\"size\":{},\"from\":{},\"sort\":{\"@timestamp\":\"asc\"},\"_source\":[]}}}}}";

    public JSON execute(String path, Method method, String json) throws IOException {
        Request request = new Request.Builder()
                .url(esApiUrl + path)
                .method(method.name(), RequestBody.create(Constant.JSON, json))
                .addHeader("Authorization", "Basic " + new String(Base64.encodeBase64((user + ":" + password).getBytes(Charsets.UTF_8))))
                .build();
        try (Response response = okHttpClient.newCall(request).execute()) {
            return response.body() == null ? null : JSONUtil.parse(response.body().string());
        }
    }

    public JSON search(Map filter, Map<String, String> sort, int from, int size) throws IOException {
        String query = StrUtil.format("{\"query\":{\"bool\":{\"filter\":{}}},\"sort\":{},\"size\":0,\"aggs\":{}}",
                JSONUtil.toJsonStr(filter),
                JSONUtil.toJsonStr(sort.entrySet().stream().map(entry -> Collections.singletonMap(entry.getKey(), entry.getValue())).collect(Collectors.toList())),
                StrUtil.format(TRACE_AGGS, size, from));
        return execute("/logstash-*/_search", Method.POST, query);
    }
}
