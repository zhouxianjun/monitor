package com.all580.monitor.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Accessors(chain = true)
@Table(name = "tab_monitor_data")
public class TabMonitorData implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 监控项
     */
    private String metric;

    /**
     * 维度KEY
     */
    private String key;

    /**
     * 维度值
     */
    private String value;

    /**
     * 主机
     */
    private String host;

    /**
     * 额外信息
     */
    private String memo;

    /**
     * 上传时间
     */
    @Column(name = "upload_time")
    private Date uploadTime;

    /**
     * 资源
     */
    private String resource;

    private static final long serialVersionUID = 1L;

}