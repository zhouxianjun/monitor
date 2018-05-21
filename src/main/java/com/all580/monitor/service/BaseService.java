package com.all580.monitor.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description:
 * @date 2018/5/2 16:30
 */
public class BaseService<T> implements IService<T> {

    @Autowired
    protected Mapper<T> mapper;

    @Override
    public T selectByKey(Object key) {
        return mapper.selectByPrimaryKey(key);
    }

    @Override
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public int save(T entity) {
        return mapper.insertSelective(entity);
    }

    @Override
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public int delete(Object key) {
        return mapper.deleteByPrimaryKey(key);
    }

    @Override
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public int updateAll(T entity) {
        return mapper.updateByPrimaryKey(entity);
    }

    @Override
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public int updateNotNull(T entity) {
        return mapper.updateByPrimaryKeySelective(entity);
    }

    @Override
    public List<T> selectByExample(Object example) {
        return mapper.selectByExample(example);
    }

    @Override
    public List<T> all() {
        return mapper.selectAll();
    }

    @Override
    public List<T> select(T entity) {
        return mapper.select(entity);
    }
}
