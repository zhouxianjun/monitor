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
     * 请求方式: GET；POST；HEAD
     */
    private String method;

    /**
     * cookie信息:JSON key:value格式
     */
    private String cookie;

    /**
     * 头信息:JSON key:value格式
     */
    private String header;

    /**
     * 认证 user:password
     */
    @Column(name = "basic_auth")
    private String basicAuth;

    /**
     * 状态
     */
    private Boolean status;

    /**
     * 报警规则ID
     */
    @Column(name = "alarm_rule_id")
    private Integer alarmRuleId;

    /**
     * 是否保存请求数据
     */
    @Column(name = "save_data")
    private Boolean saveData;

    /**
     * 请求数据脚本
     */
    private String script;

    private static final long serialVersionUID = 1L;

}
