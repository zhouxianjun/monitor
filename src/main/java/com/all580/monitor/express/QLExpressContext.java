package com.all580.monitor.express;

import com.ql.util.express.IExpressContext;
import org.springframework.context.ApplicationContext;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: 规则引擎上下文
 * @date 2018/5/16 10:11
 */
public class QLExpressContext extends HashMap<String, Object> implements IExpressContext<String, Object> {
    private ApplicationContext context;

    public QLExpressContext(ApplicationContext aContext) {
        this.context = aContext;
    }

    public QLExpressContext(Map<String, Object> aProperties,
                            ApplicationContext aContext) {
        super(aProperties);
        this.context = aContext;
    }

    /**
     * 抽象方法：根据名称从属性列表中提取属性值
     */
    @Override
    public Object get(Object name) {
        Object result = null;
        result = super.get(name);
        try {
            if (result == null && this.context != null
                    && this.context.containsBean((String) name)) {
                // 如果在Spring容器中包含bean，则返回String的Bean
                result = this.context.getBean((String) name);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return result;
    }
}

