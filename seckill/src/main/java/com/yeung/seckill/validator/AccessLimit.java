package com.yeung.seckill.validator;

import java.lang.annotation.*;

/**
 * @author Yeung Law~
 *
 * 自定义的注解
 * 实现防刷限流机制
 */

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AccessLimit {
    // 过期最大时间
    int secend() default 5;
    int maxCount() default 5;
    boolean needLogin() default true;
}
