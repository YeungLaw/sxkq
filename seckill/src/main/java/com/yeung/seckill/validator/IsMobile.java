package com.yeung.seckill.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * @author Yeung Law~
 *
 * 自定义一个注解，用来校验手机号码的格式是否有错误
 *
 * @Constraint validatedBy 表示 对该注解进行校验的类
 */

@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.TYPE, ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {IsMobileConstraintValidator.class})
public @interface IsMobile {

    String message() default "手机号码格式有误";

    boolean required() default true;
    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
