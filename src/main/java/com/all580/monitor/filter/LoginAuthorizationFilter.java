package com.all580.monitor.filter;

import cn.hutool.json.JSONUtil;
import com.all580.monitor.dto.Result;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Optional;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description:
 * @date 2018/8/30 11:29
 */
public class LoginAuthorizationFilter extends FormAuthenticationFilter {

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        res.setCharacterEncoding("UTF-8");
        res.setContentType("application/json");
        //设置允许传递的参数
        res.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, Authorization");
        //设置允许带上cookie
        res.setHeader("Access-Control-Allow-Credentials", "true");
        String origin = Optional.ofNullable(req.getHeader("Origin")).orElse(req.getHeader("Referer"));
        //设置允许的请求来源
        res.setHeader("Access-Control-Allow-Origin", origin);
        //设置允许的请求方法
        res.setHeader("Access-Control-Allow-Methods", "GET, POST, PATCH, PUT, DELETE, OPTIONS");
        try (PrintWriter writer = res.getWriter()) {
            writer.write(JSONUtil.toJsonStr(Result.builder().code(Result.NO_LOGIN).build()));
        }
        return false;
    }
}
