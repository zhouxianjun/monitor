package com.all580.monitor.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: 上报监控数据VO
 * @date 2018/5/24 16:39
 */
@Data
@ApiModel("监控数据")
public class ReportMonitorDataVo implements Serializable {

    /**
     * 监控项
     */
    @ApiModelProperty(value = "监控项", required = true, example = "host.cpu")
    private String metric;

    /**
     * 主机
     */
    @ApiModelProperty(value = "主机", example = "192.168.1.1", notes = "不传则使用请求源IP")
    private String host;

    /**
     * 批次号
     */
    @ApiModelProperty(value = "批次号", example = "1215452121242", notes = "不传则自动生成")
    private String batch;

    /**
     * 资源
     */
    @ApiModelProperty(value = "资源", required = true, example = "192.168.1.1.host", notes = "业务数据标识")
    private String resource;

    @ApiModelProperty(value = "维度", required = true)
    private List<MonitorDimensionsVo> dimensions;
}
