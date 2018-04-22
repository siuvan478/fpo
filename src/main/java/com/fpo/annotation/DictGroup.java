package com.fpo.annotation;

import java.lang.annotation.*;

/**
 * 常量注解类
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Constant
public @interface DictGroup {

    String groupName();

    String key();

    String value() default "";
}
