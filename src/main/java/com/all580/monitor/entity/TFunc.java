package com.all580.monitor.entity;

import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Data
@Accessors(chain = true)
@Table(name = "t_func")
public class TFunc implements Serializable {
    /**
     * 功能ID(func_id)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 父级ID
     */
    private Integer pid;

    /**
     * 功能名称
     */
    private String name;

    /**
     * 功能别名
     */
    private String alias;

    /**
     * 描述
     */
    private String description;

    /**
     * 排序
     */
    private Integer seq;

    /**
     * 0正常1表示删除
     */
    private Integer isdelete;

    /**
     * 状态(0:无效,1:有效)
     */
    private Integer status;

    @Column(name = "create_time")
    private Date createTime;

    /**
     * 类型(600:菜单,601:功能)
     */
    private Integer type;

    /**
     * 路径
     */
    private String path;

    /**
     * 类型为功能的ID
     */
    @Column(name = "funcId")
    private String funcid;

    /**
     * 页面tab选项卡ID
     */
    private String target;

    /**
     * 页面菜单图标样式
     */
    private String icon;

    @Column(name = "update_time")
    private Date updateTime;

    /**
     * 企业类型('0所有10001平台商10002供应商10003销售商')
     */
    @Column(name = "ep_type")
    private Integer epType;

    private static final long serialVersionUID = 1L;

}
