package com.all580.monitor.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.db.DbRuntimeException;
import com.all580.monitor.util.IMapper;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description:
 * @date 2018/5/2 16:30
 */
public class BaseService<T> implements IService<T> {

    @Autowired
    protected IMapper<T> mapper;

    @Override
    public T selectByKey(Object key) {
        return mapper.selectByPrimaryKey(key);
    }

    @Override
    public int save(T entity) {
        return mapper.insertSelective(entity);
    }

    @Override
    public int save(List<T> entity) {
        return save(entity, false);
    }

    @Override
    public int save(List<T> entity, boolean exception) {
        if (CollectionUtils.isEmpty(entity)) {
            return 0;
        }
        int ret = mapper.insertList(entity);
        if (exception && ret != entity.size()) {
            throw new DbRuntimeException("need save {}, actual save {}", entity.size(), ret);
        }
        return ret;
    }

    @Override
    public int delete(Object key) {
        return mapper.deleteByPrimaryKey(key);
    }

    @Override
    public int updateAll(T entity) {
        return mapper.updateByPrimaryKey(entity);
    }

    @Override
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
    public T selectOne(T entity) {
        return mapper.selectOne(entity);
    }

    @Override
    public T selectOneByExample(Object example) {
        return mapper.selectOneByExample(example);
    }

    @Override
    public boolean exists(T entity) {
        return selectOne(entity) != null;
    }

    @Override
    public List<T> select(T entity) {
        return mapper.select(entity);
    }

    @Override
    public List<T> selectByIds(String ids) {
        if (StringUtils.isEmpty(ids) || ids.split(",").length < 1) {
            return Collections.emptyList();
        }
        return mapper.selectByIds(ids);
    }

    @Override
    public List<T> selectByIds(Collection<Integer> ids) {
        return selectByIds(CollectionUtils.isEmpty(ids) ? "" : CollUtil.join(ids, ","));
    }

    @Override
    public List<T> selectByIds(int[] ids) {
        return selectByIds(ArrayUtils.isEmpty(ids) ? "" : ArrayUtil.join(ids, ","));
    }

    @Override
    public int deleteByIds(String ids) {
        if (StringUtils.isEmpty(ids) || ids.split(",").length < 1) {
            return 0;
        }
        return mapper.deleteByIds(ids);
    }

    @Override
    public int deleteByIds(Collection<Integer> ids) {
        return deleteByIds(CollectionUtils.isEmpty(ids) ? "" : CollUtil.join(ids, ","));
    }

    @Override
    public int deleteByIds(int[] ids) {
        return deleteByIds(ArrayUtils.isEmpty(ids) ? "" : ArrayUtil.join(ids, ","));
    }
}
