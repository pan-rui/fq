package com.hy.core;


import java.lang.annotation.*;

/**
 * Created by panrui on 2015/9/12.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD,ElementType.ANNOTATION_TYPE})
@Inherited
@Documented
public @interface DataSource {
    DataSourceHolder.DBType value() default DataSourceHolder.DBType.slave1;
}
