package com.all580.monitor.task;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.all580.monitor.Constant;
import com.all580.monitor.entity.TabAlarmRule;
import com.all580.monitor.entity.TabHttpMonitor;
import com.all580.monitor.entity.TabMonitorData;
import com.all580.monitor.express.QLExpressMgr;
import com.all580.monitor.manager.AlarmRuleManager;
import com.all580.monitor.service.AlarmRuleService;
import com.all580.monitor.service.MonitorDataService;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.codec.Charsets;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: HTTP心跳任务
 * @date 2018/5/22 9:11
 */
@Component
@Scope("prototype")
@Slf4j
public class HttpHeartbeatTaskClearTrace implements TimerTask {
    @Setter
    private TabHttpMonitor monitor;
    @Setter
    private TabAlarmRule rule;
    private Timeout timeout;
    private boolean run = true;
    @Autowired
    private MonitorDataService monitorDataService;
    @Autowired
    private AlarmRuleService alarmRuleService;
    @Autowired
    private AlarmRuleManager alarmRuleManager;
    @Autowired
    private Snowflake snowflake;
    @Autowired
    private OkHttpClient okHttpClient;
    @Autowired
    private QLExpressMgr qlExpressMgr;

    @Override
    public void run(Timeout timeout) throws Exception {
        log.debug("---开始HTTP心跳任务: {}---", monitor);
        if (rule == null) {
            rule = alarmRuleService.selectByKey(monitor.getAlarmRuleId());
        }
        final TabAlarmRule currentRule = rule;
        if (currentRule == null || !currentRule.getStatus()) {
            log.warn("HTTP心跳任务: {} 报警规则不存在", monitor);
            return;
        }
        try {
            List<TabMonitorData> data = request(currentRule);
            try {
                alarmRuleManager.judge(data, currentRule);
            } catch (Throwable e) {
                log.warn("HTTP心跳任务: {} 判定异常", monitor, e);
            }
        } catch (Throwable e) {
            log.warn("HTTP心跳任务: {} 执行异常", monitor, e);
        } finally {
            log.debug("---结束HTTP心跳任务:{} ---", monitor);
            if (this.run) {
                this.timeout = timeout.timer().newTimeout(this, currentRule.getInterval(), TimeUnit.MINUTES);
            }
        }
    }

    private List<TabMonitorData> request(TabAlarmRule currentRule) {
        String header = monitor.getHeader();
        String cookie = monitor.getCookie();
        String basicAuth = monitor.getBasicAuth();

        Request.Builder builder = new Request.Builder().url(monitor.getUrl()).method(monitor.getMethod(), null);
        if (JSONUtil.isJsonObj(header)) {
            JSONUtil.parseObj(header).forEach((key, value) -> builder.addHeader(key, String.valueOf(value)));
        }
        if (JSONUtil.isJsonObj(cookie)) {
            builder.header("Cookie", JSONUtil.parseObj(cookie).entrySet().stream()
                    .map(entry -> String.format("%s=%s", entry.getKey(), entry.getValue()))
                    .collect(Collectors.joining(";")));
        }
        if (StringUtils.isNotEmpty(basicAuth) && basicAuth.contains(":")) {
            builder.addHeader("Authorization", "Basic " + new String(Base64.encodeBase64(basicAuth.getBytes(Charsets.UTF_8))));
        }
        Request request = builder.build();
        String memo;
        int responseCode;
        Headers headers = null;
        StopWatch watch = new StopWatch();
        watch.start();
        try (Response response = okHttpClient.newCall(request).execute()) {
            try {
                if (response.body() != null) {
                    memo = response.body().string();
                } else {
                    memo = response.toString();
                }
            } catch (Exception ignored) {
                memo = response.toString();
            }
            headers = response.headers();
            responseCode = response.code();
        } catch (Exception e) {
            responseCode = HttpStatus.SC_INTERNAL_SERVER_ERROR;
            memo = e.getMessage();
        }
        watch.stop();

        List<TabMonitorData> data = getData(currentRule, memo, watch.getTotalTimeMillis(), responseCode, request.url().host(), headers);
        return save(currentRule, data);
    }

    private List<TabMonitorData> getData(TabAlarmRule currentRule, String memo, long delayMs, int responseCode, String host, Headers headers) {
        List<TabMonitorData> data = new ArrayList<>(2);
        Date uploadTime = new Date();
        String batch = String.valueOf(snowflake.nextId());
        data.add(new TabMonitorData()
                .setHost(host)
                .setMetric(Constant.Metric.HTTP_HEARTBEAT)
                .setResource(currentRule.getResource())
                .setKey("delay")
                .setValue(String.valueOf(delayMs))
                .setMemo(StrUtil.maxLength(memo, 5000))
                .setUploadTime(uploadTime)
                .setBatch(batch));

        data.add(new TabMonitorData()
                .setHost(host)
                .setMetric(Constant.Metric.HTTP_HEARTBEAT)
                .setResource(currentRule.getResource())
                .setKey("code")
                .setValue(String.valueOf(responseCode))
                .setMemo(memo)
                .setUploadTime(uploadTime)
                .setBatch(batch));
        if (StrUtil.isNotBlank(monitor.getScript())) {
            try {
                Map<String, Object> dataMap = new HashMap<>(1);
                Map<String, Object> context = MapUtil.builder(new HashMap<String, Object>(4))
                        .put("rule", rule)
                        .put("monitor", monitor)
                        .put("delay", delayMs)
                        .put("code", responseCode)
                        .put("headers", headers)
                        .put("response", memo)
                        .put("data", dataMap)
                        .put("dataList", data)
                        .put("log", log)
                        .build();
                qlExpressMgr.execute(monitor.getScript(), context);
                if (!dataMap.isEmpty()) {
                    dataMap.forEach((key, value) -> data.add(new TabMonitorData()
                            .setHost(host)
                            .setMetric(Constant.Metric.HTTP_HEARTBEAT)
                            .setResource(currentRule.getResource())
                            .setKey(key)
                            .setValue(StrUtil.utf8Str(value))
                            .setUploadTime(uploadTime)
                            .setBatch(batch)));
                }
            } catch (Throwable e) {
                log.warn("HTTP监控数据脚本异常", e);
            }
        }

        return data;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = {Exception.class, RuntimeException.class})
    public List<TabMonitorData> save(TabAlarmRule currentRule, List<TabMonitorData> data) {
        if (monitor.getSaveData()) {
            monitorDataService.save(data, true);
        }
        return data;
    }

    public void stop() {
        this.stop(false);
    }

    public void stop(boolean force) {
        this.run = false;
        if (force && timeout != null) {
            timeout.cancel();
        }
    }
}
