package com.hy.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Description: ${Description}
 * @Author: 潘锐 (2017-03-27 20:32)
 * @version: \$Rev: 2895 $
 * @UpdateAuthor: \$Author: panrui $
 * @UpdateDateTime: \$Date: 2017-06-07 17:42:18 +0800 (周三, 07 6月 2017) $
 */
@Target({ElementType.TYPE,ElementType.ANNOTATION_TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DecryptResponse {
    String value() default "application/json;charset=UTF-8";
}
