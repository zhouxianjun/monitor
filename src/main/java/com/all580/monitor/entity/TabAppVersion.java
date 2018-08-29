package com.all580.monitor.entity;

import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Data
@Accessors(chain = true)
@Table(name = "tab_app_version")
public class TabAppVersion implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 应用ID
     */
    @Column(name = "app_id")
    private Integer appId;

    /**
     * 资源ID
     */
    @Column(name = "resource_id")
    private Integer resourceId;

    /**
     * 版本号
     */
    private String version;

    @Column(name = "create_time")
    private Date createTime;

    private static final long serialVersionUID = 1L;

}
