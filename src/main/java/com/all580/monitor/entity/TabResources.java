package com.all580.monitor.entity;

import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Data
@Accessors(chain = true)
@Table(name = "tab_resources")
public class TabResources implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 校验值
     */
    private String md5;

    /**
     * 文件路径
     */
    private String path;

    /**
     * 文件大小
     */
    private Integer size;

    /**
     * 文件名
     */
    private String name;

    /**
     * 后缀
     */
    private String suffix;

    @Column(name = "create_time")
    private Date createTime;

    private static final long serialVersionUID = 1L;

}
