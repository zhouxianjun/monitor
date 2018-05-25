package com.all580.monitor.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Accessors(chain = true)
@Table(name = "tab_alarm_rule")
@ApiModel("报警规则")
public class TabAlarmRule implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(hidden = true)
    private Integer id;

    /**
     * 应用ID
     */
    @Column(name = "app_id")
    @ApiModelProperty(value = "应用ID", required = true, example = "1")
    private Integer appId;

    /**
     * 名称
     */
    @ApiModelProperty(value = "名称", required = true, example = "测试")
    private String name;

    /**
     * 监控项
     */
    @ApiModelProperty(value = "监控项", required = true, example = "host.cpu")
    private String metric;

    /**
     * 次数
     */
    @ApiModelProperty(value = "次数", required = true, example = "1", notes = "达到几次后触发报警")
    private Integer count;

    /**
     * 当前触发次数
     */
    @Column(name = "current_count")
    @ApiModelProperty(hidden = true)
    private Integer currentCount;

    /**
     * 资源
     */
    @ApiModelProperty(value = "资源", required = true, example = "192.168.1.1.host", notes = "业务数据标识")
    private String resource;

    /**
     * 频率间隔（分钟）
     */
    @ApiModelProperty(value = "频率间隔（分钟）", required = true, example = "1")
    private Integer interval;

    /**
     * 判定脚本
     */
    @ApiModelProperty(value = "判定脚本", notes = "QL规则")
    private String script;

    /**
     * 沉默间隔(分钟)
     */
    @ApiModelProperty(value = "频率间隔（分钟）", example = "1440")
    private Integer silenceInterval;

    /**
     * 报警联系人组ID
     */
    @Column(name = "alarm_group_id")
    @ApiModelProperty(value = "报警联系人组ID")
    private Integer alarmGroupId;

    /**
     * 报警回调
     */
    @Column(name = "alarm_callback")
    @ApiModelProperty(value = "报警通知回调")
    private String alarmCallback;

    /**
     * 是否启用空数据监控
     */
    @ApiModelProperty(value = "是否启用空数据监控", required = true, example = "true")
    private Boolean nodata;

    /**
     * 是否定时任务
     */
    @ApiModelProperty(hidden = true)
    private Boolean timer;

    /**
     * 状态：0--禁用；1--启用;
     */
    @ApiModelProperty(hidden = true)
    private Boolean status;

    /**
     * 最后一个报警历史ID
     */
    @Column(name = "last_history")
    @ApiModelProperty(hidden = true)
    private Integer lastHistory;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    @ApiModelProperty(hidden = true)
    private Date createTime;

    private static final long serialVersionUID = 1L;

}