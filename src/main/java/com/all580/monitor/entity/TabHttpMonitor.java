package com.all580.monitor.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Accessors(chain = true)
@Table(name = "tab_http_monitor")
public class TabHttpMonitor implements Serializable {
    /**
     * 主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 应用ID
     */
    @Column(name = "app_id")
    private Integer appId;

    /**
     * 监控名称
     */
    private String name;

    /**
     * 监控地址
     */
    private String url;

    /**
     * 监控频率(间隔)单位为分钟
     */
    private Integer interval;

    /**
     * 请求方式: 1--GET；2--POST；3--HEAD
     */
    private Integer method;

    /**
     * cookie信息:JSON key:value格式
     */
    private String cookie;

    /**
     * 头信息:JSON key:value格式
     */
    private String header;

    /**
     * 状态
     */
    private Boolean status;

    /**
     * 报警规则ID
     */
    @Column(name = "alarm_rule_id")
    private Integer alarmRuleId;

    private static final long serialVersionUID = 1L;

}