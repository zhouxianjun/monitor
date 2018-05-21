package com.all580.monitor.config;

import cn.hutool.core.date.DateUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: mvc 前端日期参数处理
 * @date 2018/5/17 9:07
 */
@Component
public class DateConverterConfig implements Converter<String, Date> {

    @Override
    public Date convert(String s) {
        String value = s.trim();
        if (StringUtils.isEmpty(value)) {
            return null;
        }
        return DateUtil.parse(value);
    }
}
