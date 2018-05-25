package com.all580.monitor.express;

import com.ql.util.express.ExpressRunner;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: 规则引擎管理
 * @date 2018/5/16 10:12
 */
@Slf4j
@Component
public class QLExpressMgr {
    @Setter
    private ApplicationContext applicationContext;
    private static final String COMMENT = "\\/\\/[^\\n]*|\\/\\*([^\\*^\\/]*|[\\*^\\/*]*|[^\\**\\/]*)*\\*\\/";
    protected static final ExpressRunner runner;

    static {
        runner = new ExpressRunner();
    }

    /**
     * 执行脚本
     * @param script 脚本
     * @param context 上下文
     * @return
     * @throws Throwable
     */
    public Object execute(String script, Map<String, Object> context) throws Throwable {
        Object execute = runner.execute(script.replaceAll(COMMENT, ""), new QLExpressContext(context, applicationContext), null, true, false);
        if (execute instanceof Throwable) {
            throw (Throwable) execute;
        }
        return execute;
    }

    /**
     * 执行脚本
     * @param script 脚本
     * @return
     * @throws Throwable
     */
    public Object execute(String script) throws Throwable {
        Object execute = runner.execute(script.replaceAll(COMMENT, ""), new QLExpressContext(applicationContext), null, true, false);
        if (execute instanceof Throwable) {
            throw (Throwable) execute;
        }
        return execute;
    }

    /**
     * 批量执行脚本
     * @param context 上下文
     * @param scripts 脚本列表
     * @return
     * @throws Throwable
     */
    public List<Object> execute(Map<String, Object> context, String... scripts) throws Throwable {
        List<Object> result = new ArrayList<>();
        for (String script : scripts) {
            if (StringUtils.isNotEmpty(script)) {
                Object ret = execute(script.replaceAll(COMMENT, ""), context);
                result.add(ret);
            }
        }

        return result;
    }

    public boolean validate(String script) {
        try {
            runner.parseInstructionSet(script.replaceAll(COMMENT, ""));
            return true;
        } catch (Exception e) {
            log.debug("验证脚本: {} 失败", script);
            return false;
        }
    }
}
