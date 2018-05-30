package com.all580.monitor.task;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.all580.monitor.Constant;
import com.all580.monitor.entity.TabAlarmRule;
import com.all580.monitor.entity.TabHttpMonitor;
import com.all580.monitor.entity.TabMonitorData;
import com.all580.monitor.manager.AlarmRuleManager;
import com.all580.monitor.service.AlarmRuleService;
import com.all580.monitor.service.MonitorDataService;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
        List<TabMonitorData> data = new ArrayList<>(2);
        try {
            String header = monitor.getHeader();
            String cookie = monitor.getCookie();
            String basicAuth = monitor.getBasicAuth();
            long start = System.currentTimeMillis();
            String memo;
            long delayMs;
            int responseCode;

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
            try {
                Response response = okHttpClient.newCall(request).execute();
                try {
                    if (response.body() != null) {
                        memo = response.body().string();
                    } else {
                        memo = response.toString();
                    }
                } catch (Exception ignored) {
                    memo = response.toString();
                }
                responseCode = response.code();
            } catch (Exception e) {
                responseCode = HttpStatus.SC_INTERNAL_SERVER_ERROR;
                memo = e.getMessage();
            }
            delayMs = System.currentTimeMillis() - start;
            Date uploadTime = new Date();
            long batch = snowflake.nextId();
            TabMonitorData delay = new TabMonitorData()
                    .setHost(request.url().host())
                    .setMetric(Constant.Metric.HTTP_HEARTBEAT)
                    .setResource(currentRule.getResource())
                    .setKey("delay")
                    .setValue(String.valueOf(delayMs))
                    .setMemo(StrUtil.maxLength(memo, 5000))
                    .setUploadTime(uploadTime)
                    .setBatch(String.valueOf(batch));
            monitorDataService.save(delay);
            data.add(delay);
            TabMonitorData code = new TabMonitorData()
                    .setHost(request.url().host())
                    .setMetric(Constant.Metric.HTTP_HEARTBEAT)
                    .setResource(currentRule.getResource())
                    .setKey("code")
                    .setValue(String.valueOf(responseCode))
                    .setMemo(memo)
                    .setUploadTime(uploadTime)
                    .setBatch(String.valueOf(batch));
            monitorDataService.save(code);
            data.add(code);
        } catch (Throwable e) {
            log.warn("HTTP心跳任务: {} 执行异常", monitor, e);
        } finally {
            ThreadUtil.execute(() -> {
                try {
                    alarmRuleManager.judge(data, currentRule);
                } catch (Throwable e) {
                    log.warn("HTTP心跳任务: {} 判定异常", monitor, e);
                }
            });
            log.debug("---结束HTTP心跳任务:{} ---", monitor);
            if (this.run) {
                this.timeout = timeout.timer().newTimeout(this, currentRule.getInterval(), TimeUnit.MINUTES);
            }
        }
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