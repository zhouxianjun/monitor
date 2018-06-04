package com.all580.monitor.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Accessors(chain = true)
@Table(name = "tab_es_watch_job")
public class TabEsWatchJob implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 日志监控ID
     */
    @Column(name = "watcher_id")
    private String watcherId;

    /**
     * traceid 逗号分隔
     */
    private String trace;

    /**
     * 是否执行
     */
    private Boolean status;

    @Column(name = "create_time")
    private Date createTime;

    private static final long serialVersionUID = 1L;

}