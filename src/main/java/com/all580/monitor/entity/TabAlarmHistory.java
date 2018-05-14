package com.all580.monitor.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Accessors(chain = true)
@Table(name = "tab_alarm_history")
public class TabAlarmHistory implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 规则ID
     */
    @Column(name = "rule_id")
    private Integer ruleId;

    /**
     * 持续时间(分)
     */
    private Integer duration;

    /**
     * 发生时间
     */
    @Column(name = "create_time")
    private Date createTime;

    /**
     * 状态:1-通道沉默；2-报警发生；3-恢复正常
     */
    private Integer status;

    private static final long serialVersionUID = 1L;

}