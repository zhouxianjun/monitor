package com.all580.monitor.util;

import tk.mybatis.mapper.common.IdsMapper;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: 通用Mapper
 * @date 2018/5/25 10:33
 */
public interface IMapper<T> extends Mapper<T>, MySqlMapper<T>, IdsMapper<T> {
}
