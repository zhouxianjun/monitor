package com.all580.monitor.aop;

import cn.hutool.core.util.StrUtil;
import com.all580.monitor.entity.TabApp;
import com.all580.monitor.manager.BusinessManager;
import com.all580.monitor.service.AppService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.lang.reflect.Method;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description:
 * @date 2018/7/3 9:42
 */
@Component
@Aspect
public class AppBusinessServiceAop {
    private static final String BUSINESS_APP = "_BUSINESS_APP";
    @Autowired
    private AppService appService;
    @Autowired
    private BusinessManager businessManager;

    @Pointcut(value = "@within(com.all580.monitor.annotation.Business)")
    public void inBusiness(){}

    @Pointcut(value = "@within(org.springframework.web.bind.annotation.RequestMapping)")
    public void inRequestMapping(){}

    @Pointcut(value = "execution(* com.all580.monitor.controller.business.*.*(int, ..)) && args(appId, ..)", argNames = "appId")
    public void executionCut(int appId){}

    @Before(value = "inBusiness() && inRequestMapping() && executionCut(appId)", argNames = "joinPoint,appId")
    public void doBefore(JoinPoint joinPoint, int appId){
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        boolean is = method.getParameters()[0].isAnnotationPresent(PathVariable.class);
        if (!is) {
            return;
        }
        String path = joinPoint.getTarget().getClass().getAnnotation(RequestMapping.class).value()[0];
        TabApp app = appService.selectByKey(appId);
        if (app == null) {
            throw new RuntimeException(StrUtil.format("业务接口: {} 应用为空", path));
        }
        if (!businessManager.isTypePresent(path, app.getType())) {
            throw new RuntimeException(StrUtil.format("业务接口: {} 不能被应用类型: {} 访问", path, app.getType()));
        }
        RequestContextHolder.currentRequestAttributes().setAttribute(BUSINESS_APP, app, RequestAttributes.SCOPE_REQUEST);
    }

    public static TabApp getApp() {
        return (TabApp) RequestContextHolder.currentRequestAttributes().getAttribute(BUSINESS_APP, RequestAttributes.SCOPE_REQUEST);
    }
}
