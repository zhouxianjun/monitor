package com.all580.monitor.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description:
 * @date 2018/5/16 13:10
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface NoticeType {
    int value();
}
