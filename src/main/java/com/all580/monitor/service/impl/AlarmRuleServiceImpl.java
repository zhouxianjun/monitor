package com.all580.monitor.service.impl;

import com.all580.monitor.entity.TabAlarmRule;
import com.all580.monitor.express.QLExpressMgr;
import com.all580.monitor.mapper.TabAlarmRuleMapper;
import com.all580.monitor.service.AlarmRuleService;
import com.all580.monitor.service.BaseService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description:
 * @date 2018/5/15 16:34
 */
@Service
public class AlarmRuleServiceImpl extends BaseService<TabAlarmRule> implements AlarmRuleService {
    @Resource
    private TabAlarmRuleMapper tabAlarmRuleMapper;
    @Autowired
    private QLExpressMgr qlExpressMgr;

    @Override
    public List<?> list(Integer spot, Integer app, String name, Boolean alarm, Boolean status) {
        return tabAlarmRuleMapper.search(spot, app, name, alarm != null && alarm ? true : null, status);
    }

    @Override
    public int save(TabAlarmRule entity) {
        if (entity.getCreateTime() == null) {
            entity.setCreateTime(new Date());
        }
        if (StringUtils.isNotEmpty(entity.getScript()) && !qlExpressMgr.validate(entity.getScript())) {
            throw new RuntimeException("脚本错误");
        }
        return super.save(entity);
    }

    @Override
    public int updateNotNull(TabAlarmRule entity) {
        if (StringUtils.isNotEmpty(entity.getScript()) && !qlExpressMgr.validate(entity.getScript())) {
            throw new RuntimeException("脚本错误");
        }
        return super.updateNotNull(entity);
    }
}
