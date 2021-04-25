package org.lyfy.beyond.ratelimiter.annotation;


import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimit {

    /**
     * 支持普通字符串和EL表达式，不设置则默认使用当前类名和方法名的组合作为key值。
     * 注意：
     * 1. 如果使用guava类型，建议不写
     * 2. 如果使用EL表达式，则建议一定使用redis类型避免key的值过多导致jvm内存溢出
     *
     */
    String key() default "";

    /**
     * 需要设置的频次大小限制
     *
     */
    int limitNum() default 100;

    /**
     * 建议不设置。使用默认值，即1秒
     *
     */
    int seconds() default 1;

}
