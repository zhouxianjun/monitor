package com.all580.monitor.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Accessors(chain = true)
@Table(name = "tab_es_watch_log")
public class TabEsWatchLog implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * watch id
     */
    @Column(name = "watch_id")
    private Integer watchId;

    /**
     * 主机地址
     */
    private String host;

    /**
     * 日志文件
     */
    private String source;

    /**
     * 原始日志
     */
    private String msg;

    /**
     * 原始日志时间
     */
    @Column(name = "log_time")
    private Date logTime;

    /**
     * ES时间
     */
    @Column(name = "sys_time")
    private Date sysTime;

    private static final long serialVersionUID = 1L;

}