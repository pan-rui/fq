package com.hy.annotation;

import java.lang.annotation.*;

/**
 * 交易流水记录
 * Created by lenovo on 2014/12/18.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Documented
public @interface TradeLog {
    String value() default "";
}
