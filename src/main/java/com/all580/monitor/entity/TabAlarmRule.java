package com.all580.monitor.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Accessors(chain = true)
@Table(name = "tab_alarm_rule")
public class TabAlarmRule implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 应用ID
     */
    @Column(name = "app_id")
    private Integer appId;

    /**
     * 名称
     */
    private String name;

    /**
     * 监控项
     */
    private String metric;

    /**
     * 次数
     */
    private Integer count;

    /**
     * 当前触发次数
     */
    @Column(name = "current_count")
    private Integer currentCount;

    /**
     * 资源
     */
    private String resource;

    /**
     * 频率间隔（分钟）
     */
    private Integer interval;

    /**
     * 判定脚本
     */
    private String script;

    /**
     * 沉默间隔(分钟)
     */
    @Column(name = "silence_interval")
    private Integer silenceInterval;

    /**
     * 报警联系人组ID
     */
    @Column(name = "alarm_group_id")
    private Integer alarmGroupId;

    /**
     * 报警回调
     */
    @Column(name = "alarm_callback")
    private String alarmCallback;

    /**
     * 是否启用空数据监控
     */
    private Boolean nodata;

    /**
     * 状态：0--禁用；1--启用;
     */
    private Boolean status;

    /**
     * 最后一个报警历史ID
     */
    @Column(name = "last_history")
    private Integer lastHistory;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private Date createTime;

    private static final long serialVersionUID = 1L;

}