package org.lyfy.beyond.distributed.lock.anno;

import java.lang.annotation.*;

/**
 * @author: 谢星星
 * @Date: 2019/9/16 11:17
 * @Description:
 */
@Target({ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DistributedLock {

    /**
     * 被锁定的key值，支持spel
     */
    String key();

    /**
     * 获得锁之前的等待时间，（单位）秒
     */
    int waitTime() default 3;

    /**
     * 获得锁之后的执行时间，防止一个现成一直持有着锁不释放，（单位）秒
     */
    int leaseTime() default 5;
}
