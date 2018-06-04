package com.all580.monitor.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Accessors(chain = true)
@Table(name = "tab_es_watch")
public class TabEsWatch implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 应用ID
     */
    @Column(name = "app_id")
    private Integer appId;

    /**
     * ES预计ID
     */
    @Column(name = "watcher_id")
    private String watcherId;

    /**
     * 名称
     */
    private String name;

    /**
     * 配置信息JSON
     */
    private String config;

    /**
     * 描述
     */
    private String memo;

    /**
     * 状态
     */
    private Boolean status;

    /**
     * 是否上报
     */
    private Boolean reported;

    /**
     * 上报脚本
     */
    @Column(name = "reported_script")
    private String reportedScript;

    private static final long serialVersionUID = 1L;

}