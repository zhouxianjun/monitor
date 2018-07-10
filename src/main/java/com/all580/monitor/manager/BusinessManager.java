package com.all580.monitor.manager;

import cn.hutool.json.JSONUtil;
import com.all580.monitor.annotation.Business;
import com.all580.monitor.config.OkHttpConfig;
import com.all580.monitor.dto.Result;
import com.all580.monitor.entity.TabApp;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import lombok.Getter;
import lombok.SneakyThrows;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description:
 * @date 2018/7/3 14:12
 */
@Component
public class BusinessManager implements BeanPostProcessor {
    @Autowired
    private OkHttpClient okHttpClient;
    @Getter
    private Multimap<String, Integer> businessMap = HashMultimap.create();

    @SneakyThrows
    public Result<?> proxy(TabApp app, String url, String method, String params) {
        RequestBody body = JSONUtil.isJson(params) && !"GET".equals(method) ? RequestBody.create(OkHttpConfig.JSON, params) : null;
        Request.Builder builder = new Request.Builder()
                .url(url.startsWith("http") ? url : app.getService() + url)
                .method(method, body)
                .addHeader("sign", DigestUtils.md5Hex(app.getAuthKey()))
                .addHeader("auth_id", app.getAuthId());

        try (Response response = okHttpClient.newCall(builder.build()).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                return Result.builder().code(Result.SUCCESS).value(JSONUtil.toBean(response.body().string(), Map.class)).build();
            }
            return Result.builder().code(Result.FAIL).value(response).build();
        }
    }

    public Result<?> proxy(TabApp app, String url, String method, Object params) {
        return proxy(app, url, method, !"GET".equals(method) ? JSONUtil.toJsonStr(params) : null);
    }

    public boolean isTypePresent(String path, Integer type) {
        if (!businessMap.containsKey(path)) {
            return false;
        }
        Collection<Integer> types = businessMap.get(path);
        return CollectionUtils.isEmpty(types) || types.iterator().next() == null || types.contains(type);
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> aClass = AopUtils.getTargetClass(bean);
        if (aClass.isAnnotationPresent(Business.class) && aClass.isAnnotationPresent(RequestMapping.class)) {
            String path = aClass.getAnnotation(RequestMapping.class).value()[0];
            int[] types = aClass.getAnnotation(Business.class).types();
            if (types.length > 0) {
                Arrays.stream(types).forEach(type -> businessMap.put(path, type));
            } else {
                businessMap.put(path, null);
            }
        }
        return bean;
    }
}
