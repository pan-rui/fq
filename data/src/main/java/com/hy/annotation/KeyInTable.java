package com.hy.annotation;


import com.hy.annotation.impl.KeyInTableCheck;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by lenovo on 2014/12/8.
 */
@Inherited
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.METHOD})
@java.lang.annotation.Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = KeyInTableCheck.class)
public @interface KeyInTable {
    String message() default "{data.inValidLinkTable}";
//    Class<? extends IBaseDao> value();
    String value();
    String column() default "";
    Class<? extends Payload>[] payload() default {};
    Class<?>[] groups() default {};

}
