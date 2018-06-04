package com.all580.monitor.service;

import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description:
 * @date 2018/5/2 16:28
 */
public interface IService<T> {
    T selectByKey(Object key);

    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    int save(T entity);

    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    int save(List<T> entity);

    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    int save(List<T> entity, boolean exception);

    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    int delete(Object key);

    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    int updateAll(T entity);

    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    int updateNotNull(T entity);

    List<T> selectByExample(Object example);

    List<T> all();

    T selectOne(T entity);

    List<T> select(T entity);

    List<T> selectByIds(String ids);

    List<T> selectByIds(Collection<Integer> ids);

    List<T> selectByIds(int[] ids);

    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    int deleteByIds(String ids);

    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    int deleteByIds(Collection<Integer> ids);

    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    int deleteByIds(int[] ids);
}
