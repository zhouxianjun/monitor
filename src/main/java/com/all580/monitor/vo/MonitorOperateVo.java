package com.all580.monitor.vo;

import com.all580.monitor.entity.TabAlarmRule;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: 监控操作VO
 * @date 2018/5/25 11:29
 */
@Data
@ApiModel("监控操作数据")
public class MonitorOperateVo<T> implements Serializable {
    @ApiModelProperty(value = "监控配置", required = true)
    private T monitor;
    @ApiModelProperty(value = "报警规则", required = true)
    private TabAlarmRule rule;
}
