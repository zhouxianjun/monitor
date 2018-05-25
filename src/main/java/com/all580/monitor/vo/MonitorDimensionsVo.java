package com.all580.monitor.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description:
 * @date 2018/5/24 17:57
 */
@Data
@ApiModel("监控数据-维度")
public class MonitorDimensionsVo implements Serializable {
    @ApiModelProperty(value = "维度KEY", required = true, example = "percent")
    private String key;
    @ApiModelProperty(value = "维度值", required = true, example = "50")
    private Object value;
    @ApiModelProperty(value = "额外信息", example = "disk_1: 90")
    private String memo;
}
