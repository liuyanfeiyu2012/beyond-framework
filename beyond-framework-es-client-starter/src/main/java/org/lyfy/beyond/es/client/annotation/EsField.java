package org.lyfy.beyond.es.client.annotation;

import java.lang.annotation.*;

/**
 * @author: 谢星星
 * @date: 2019/12/18 13:58
 * @description:
 */
@Target({ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EsField {

    /**
     * ES中Nest类型对应的字段的父path
     */
    String parentPath() default "";

    /**
     * ES中对应的字段
     */
    String path() default "";
}
