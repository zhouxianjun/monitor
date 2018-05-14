package com.all580.monitor.service.impl;

import com.all580.monitor.entity.TabApp;
import com.all580.monitor.service.AppService;
import com.all580.monitor.service.BaseService;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description:
 * @date 2018/5/11 9:40
 */
@Service
public class AppServiceImpl extends BaseService<TabApp> implements AppService {
    @Override
    public List<TabApp> list(Integer spot, String name) {
        Example example = new Example(TabApp.class);
        if (spot != null) {
            example.and().andEqualTo("spotId", spot);
        }
        if (StringUtils.isNotEmpty(name)) {
            example.and().andLike("name", "%" + name + "%");
        }
        return mapper.selectByExample(example);
    }
}
