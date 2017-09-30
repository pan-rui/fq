package com.hy.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by hynpublic on 2015/1/5.
 * 防止重复提交注解，用于方法上<br/>
 * 在新建页面方法上，设置saveToken()为true，此时拦截器会在Session中保存一个token， 同时需要在新建的页面中添加<input
 * type="hidden" name="token" value="${token}"><br/>
 * 保存方法需要验证重复提交的，设置removeToken为true 此时会在拦截器中验证是否重复提交
 *
*/
@Target({ElementType.ANNOTATION_TYPE, ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AvoidSubmits {
    boolean saveToken() default false;
    boolean removeToken() default false;
}
