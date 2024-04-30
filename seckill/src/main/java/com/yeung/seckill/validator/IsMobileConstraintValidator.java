package com.yeung.seckill.validator;

import com.yeung.seckill.util.ValidatorUtil;
import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author Yeung Law~
 *
 * isMobileConstraintValidator 是真正校验电话号码格式的类
 * <isMobile, String> 是针对传入 @isMobile注解的 String 类型数据进行校验
 */
public class IsMobileConstraintValidator implements ConstraintValidator<IsMobile, String> {

    private boolean required = false;

    @Override
    public void initialize(IsMobile constraintAnnotation) {
        required = constraintAnnotation.required();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {

        if (required) {
            // 直接进行验证
            return ValidatorUtil.isMobile(value);
        }else {
            if (!StringUtils.hasText(value)) {
                return true;
            }else {
                return ValidatorUtil.isMobile(value);
            }
        }
    }
}
