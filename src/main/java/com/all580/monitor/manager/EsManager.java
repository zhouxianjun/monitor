package com.all580.monitor.manager;

import cn.hutool.core.map.MapUtil;
import cn.hutool.http.Method;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import com.all580.monitor.Constant;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.apache.commons.codec.Charsets;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: elasticsearch
 * @date 2018/5/28 11:44
 */
@Component
@Slf4j
public class EsManager {
    @Autowired
    private OkHttpClient okHttpClient;
    @Value("${es.api.url}")
    private String esApiUrl;
    @Value("${es.api.user}")
    private String user;
    @Value("${es.api.password}")
    private String password;

    public static final String QUERY_TRACE = "{\"_source\":[\"systemmsg\",\"cusmsg\",\"offset\",\"_id\",\"remote_ip\",\"@timestamp\",\"source\",\"trace_id\"],\"query\":{\"bool\":{\"filter\":[{\"terms\":{\"trace_id\":[]}}]}},\"from\":0,\"size\":100,\"sort\":[{\"@timestamp\":\"asc\"},{\"offset\":\"asc\"}]}";

    public JSON execute(String path, Method method, String json) throws IOException {
        Request request = new Request.Builder()
                .url(esApiUrl + path)
                .method(method.name(),StringUtils.isEmpty(json) ? null : RequestBody.create(Constant.JSON, json))
                .addHeader("Authorization", "Basic " + new String(Base64.encodeBase64((user + ":" + password).getBytes(Charsets.UTF_8))))
                .build();
        log.debug("curl {}, body: {}", path, json);
        try (Response response = okHttpClient.newCall(request).execute()) {
            return response.body() == null ? JSONUtil.parseObj("{\"error\": \""+response.toString()+"\"}") : JSONUtil.parse(response.body().string());
        }
    }

    @SneakyThrows
    public List<Map<String, Object>> queryTrace(List<String> traces) {
        DocumentContext parse = JsonPath.parse(EsManager.QUERY_TRACE);
        parse.set("$.query.bool.filter[0].terms.trace_id", traces);
        List<Map<String, Object>> list = new ArrayList<>();
        queryTrace(parse, list);
        return list;
    }

    @SneakyThrows
    private List<Map<String, Object>> queryTrace(DocumentContext parse, List<Map<String, Object>> list) {
        parse.set("$.from", list.size());
        JSON json = execute("/logstash-*/_search", Method.POST, parse.jsonString());
        Object error = json.getByPath("$.error");
        if (error != null) {
            throw new RuntimeException(error.toString());
        }
        DocumentContext resultParse = JsonPath.parse(json.toString());
        int total = resultParse.read("$.hits.total");
        if (total <= 0) {
            return list;
        }
        List<Map<String, Object>> hits = resultParse.read("$.hits.hits[*]");
        hits = hits.stream()
                .map(map -> MapUtil.builder("_id", map.get("_id"))
                        .putAll((Map<String, Object>) map.get("_source"))
                        .build())
                .collect(Collectors.toList());
        list.addAll(hits);
        if (list.size() < total) {
            return queryTrace(parse, list);
        }
        return list;
    }
}
