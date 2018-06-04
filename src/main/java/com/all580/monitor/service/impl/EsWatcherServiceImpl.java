package com.all580.monitor.service.impl;

import cn.hutool.http.Method;
import cn.hutool.json.JSON;
import com.all580.monitor.entity.TabEsWatch;
import com.all580.monitor.manager.EsManager;
import com.all580.monitor.mapper.TabEsWatchMapper;
import com.all580.monitor.service.BaseService;
import com.all580.monitor.service.EsWatcherService;
import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description:
 * @date 2018/5/31 11:11
 */
@Service
public class EsWatcherServiceImpl extends BaseService<TabEsWatch> implements EsWatcherService {
    @Resource
    private TabEsWatchMapper tabEsWatchMapper;
    @Autowired
    private EsManager esManager;

    @Override
    public List<?> list(Integer spot, Integer app, String name, Boolean status) {
        return tabEsWatchMapper.search(spot, app, name, status);
    }

    @Override
    @SneakyThrows
    public int save(TabEsWatch entity) {
        entity.setWatcherId(PinyinHelper.convertToPinyinString(entity.getName(), "_", PinyinFormat.WITH_TONE_NUMBER));
        int ret = super.save(entity);
        if (ret > 0) {
            executeWatcher(entity.getWatcherId(), Method.PUT, entity.getConfig());
        }
        return ret;
    }

    @Override
    @SneakyThrows
    public int updateNotNull(TabEsWatch entity) {
        entity.setWatcherId(PinyinHelper.convertToPinyinString(entity.getName(), "_", PinyinFormat.WITH_TONE_NUMBER));
        int ret = super.updateNotNull(entity);
        if (ret > 0) {
            executeWatcher(entity.getWatcherId(), entity.getStatus() ? Method.PUT : Method.DELETE, entity.getStatus() ? entity.getConfig() : null);
        }
        return ret;
    }

    @Override
    public int delete(Object key) {
        TabEsWatch entity = selectByKey(key);
        if (entity == null) {
            return 0;
        }
        int ret = super.delete(key);
        if (ret > 0) {
            executeWatcher(entity.getWatcherId(), Method.DELETE, null);
        }
        return ret;
    }

    @SneakyThrows
    private void executeWatcher(String id, Method method, String config) {
        JSON json = esManager.execute("/_xpack/watcher/watch/" + id, method, config);
        Object error = json.getByPath("error");
        if (error != null) {
            throw new RuntimeException(error.toString());
        }
    }
}
