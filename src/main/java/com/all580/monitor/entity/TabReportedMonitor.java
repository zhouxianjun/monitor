package com.all580.monitor.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Accessors(chain = true)
@Table(name = "tab_reported_monitor")
public class TabReportedMonitor implements Serializable {
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