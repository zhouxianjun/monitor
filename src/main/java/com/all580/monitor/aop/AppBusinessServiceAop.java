package com.all580.monitor.aop;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.all580.monitor.entity.TabApp;
import com.all580.monitor.manager.BusinessManager;
import com.all580.monitor.service.AppService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description:
 * @date 2018/7/3 9:42
 */
@Component
@Aspect
public class AppBusinessServiceAop {
    @Autowired
    private AppService appService;
    @Autowired
    private BusinessManager businessManager;

    @Pointcut(value = "@within(com.all580.monitor.annotation.Business) && @within(org.springframework.web.bind.annotation.RequestMapping) && args(appId, app, ..)", argNames = "appId,app")
    public void pointCut(int appId, TabApp app){}

    @Before(value = "pointCut(appId, app)", argNames = "joinPoint,appId,app")
    public void doBefore(JoinPoint joinPoint, int appId, TabApp app){
        TabApp tabApp = appService.selectByKey(appId);
        BeanUtil.copyProperties(tabApp, app);
        String path = joinPoint.getTarget().getClass().getAnnotation(RequestMapping.class).value()[0];
        if (!businessManager.isTypePresent(path, app.getType())) {
            throw new RuntimeException(StrUtil.format("业务接口: {} 不能被应用类型: {} 访问", path, app.getType()));
        }
    }
}
