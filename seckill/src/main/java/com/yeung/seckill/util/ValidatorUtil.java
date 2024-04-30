package com.yeung.seckill.util;

import org.junit.jupiter.api.Test;
import org.springframework.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Yeung Law~
 *
 * 用来完成 各种校验规则
 */
public class  ValidatorUtil {

    // 手机号码校验格式 130 0000 0000
    private static final Pattern mobile_pattern = Pattern.compile("^[1][3-9][0-9]{9}");

    /**
     * 判断手机号码格式 是否正确
     * @param mobile 手机号码
     * @return
     */
    public static boolean isMobile(String mobile) {
        if (!StringUtils.hasText(mobile)) {
            return false;
        }else {
            Matcher matcher = mobile_pattern.matcher(mobile);
            return matcher.matches();
        }
    }

    // @Test
    // public void t1() {
    //     System.out.println(isMobile("21315870762"));
    // }
}
