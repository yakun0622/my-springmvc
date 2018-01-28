package com.acronsh.annotation;

import java.lang.annotation.*;

/**
 * @author wangyakun
 * @email yakun0622@gmail.com
 * @date 2018/1/28 21:00
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestMapping {
    String value();
}
