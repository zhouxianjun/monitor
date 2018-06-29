package com.all580.monitor.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Accessors(chain = true)
@Table(name = "tab_app")
public class TabApp implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 景区ID
     */
    @Column(name = "spot_id")
    @ApiModelProperty("景区ID")
    private Integer spotId;

    /**
     * 名称
     */
    @ApiModelProperty("应用名称")
    private String name;

    /**
     * 主机列表（逗号分隔）
     */
    @ApiModelProperty("主机列表（逗号分隔）")
    private String hosts;

    /**
     * 是否报警
     */
    @ApiModelProperty("是否报警")
    private Boolean alarm;

    @ApiModelProperty("应用授权ID")
    private String authId;

    @ApiModelProperty("应用授权KEY")
    private String authKey;

    @ApiModelProperty("应用服务接口")
    private String service;

    @ApiModelProperty("应用类型")
    private Integer type;

    @Column(name = "create_time")
    private Date createTime;

    private static final long serialVersionUID = 1L;

}
