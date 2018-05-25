package com.all580.monitor.service;

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

    int save(T entity);

    int save(List<T> entity);

    int save(List<T> entity, boolean exception);

    int delete(Object key);

    int updateAll(T entity);

    int updateNotNull(T entity);

    List<T> selectByExample(Object example);

    List<T> all();

    List<T> select(T entity);

    List<T> selectByIds(String ids);

    List<T> selectByIds(Collection<Integer> ids);

    List<T> selectByIds(int[] ids);

    int deleteByIds(String ids);

    int deleteByIds(Collection<Integer> ids);

    int deleteByIds(int[] ids);
}
