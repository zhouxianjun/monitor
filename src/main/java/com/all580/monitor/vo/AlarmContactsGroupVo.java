package com.all580.monitor.vo;

import com.all580.monitor.entity.TabAlarmContactsGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: 联系人组VO
 * @date 2018/6/6 16:28
 */
@Data
@ApiModel("报警联系人组")
public class AlarmContactsGroupVo implements Serializable {
    @ApiModelProperty(value = "组信息", required = true)
    private TabAlarmContactsGroup group;
    @ApiModelProperty(value = "联系人列表", required = true)
    private List<Integer> contacts;
}
