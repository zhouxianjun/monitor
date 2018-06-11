package com.all580.monitor.task;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.all580.monitor.entity.TabEsWatch;
import com.all580.monitor.entity.TabEsWatchJob;
import com.all580.monitor.entity.TabEsWatchLog;
import com.all580.monitor.manager.EsManager;
import com.all580.monitor.service.EsWatcherJobService;
import com.all580.monitor.service.EsWatcherLogService;
import com.all580.monitor.service.EsWatcherService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description:预警任务
 * @date 2018/6/1 9:40
 */
@Component
@Slf4j
public class WatcherJobTaskClearTrace {
    private AtomicBoolean run = new AtomicBoolean(false);
    @Autowired
    private EsWatcherJobService esWatcherJobService;
    @Autowired
    private EsWatcherService esWatcherService;
    @Autowired
    private EsWatcherLogService esWatcherLogService;
    @Autowired
    private EsManager esManager;
    private static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    static {
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    @Scheduled(fixedDelay = 60 * 1000 * 5)
    public void run() {
        if (run.compareAndSet(false, true)) {
            try {
                Example example = new Example(TabEsWatchJob.class);
                example.and().andEqualTo("status", false).andLessThanOrEqualTo("createTime", DateUtils.addMinutes(new Date(), -5));
                List<TabEsWatchJob> list = esWatcherJobService.selectByExample(example);
                if (list != null) {
                    list.forEach(job -> {
                        TabEsWatch watch = esWatcherService.selectOne(new TabEsWatch().setWatcherId(job.getWatcherId()));
                        if (watch != null) {
                            List<Map<String, Object>> maps = esManager.queryTrace(Arrays.stream(job.getTrace().split(",")).collect(Collectors.toList()));
                            maps.forEach(map -> {
                                try {
                                    String logId = map.get("_id").toString();
                                    Stream<String> logIdStream = Arrays.stream(job.getLogId().split(","));
                                    esWatcherLogService.save(new TabEsWatchLog()
                                            .setHost(map.get("remote_ip").toString())
                                            .setLogTime(df.parse(map.get("@timestamp").toString()))
                                            .setCreateTime(new Date())
                                            .setSource(map.get("source").toString())
                                            .setTraceId(map.get("trace_id").toString())
                                            .setWatchId(watch.getId())
                                            .setLogId(logId)
                                            .setOffset(MapUtil.getInt(map, "offset"))
                                            .setMarker(logIdStream.anyMatch(id -> id.equals(logId)))
                                            .setMsg(StrUtil.maxLength(StrUtil.format("{}　{}", map.get("systemmsg"), map.get("cusmsg")), 4500)));
                                } catch (DuplicateKeyException dbe) {
                                    log.warn("当前日志已经记录: {}", map);
                                } catch (ParseException e) {
                                    throw new RuntimeException(e);
                                }
                            });
                            job.setStatus(true);
                            esWatcherJobService.updateNotNull(job);
                        }
                    });
                }
            } finally {
                run.set(false);
            }
        }
    }
}
