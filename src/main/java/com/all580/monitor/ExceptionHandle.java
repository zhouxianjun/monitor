package com.all580.monitor;

import com.all580.monitor.dto.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: 统一异常处理
 * @date 2018/5/24 16:54
 */
@ControllerAdvice
@Slf4j
public class ExceptionHandle {
    @ExceptionHandler(RuntimeException.class)
    @ResponseBody
    public Result<?> handle(RuntimeException e, HttpServletRequest request){
        log.error("未捕获的异常", e);
        return Result.builder()
                .code(Result.FAIL)
                .msg("操作失败")
                .build()
                .put("timestamp", System.currentTimeMillis())
                .put("message", ExceptionUtils.getStackTrace(e))
                .put("path", request.getRequestURI());
    }
}
