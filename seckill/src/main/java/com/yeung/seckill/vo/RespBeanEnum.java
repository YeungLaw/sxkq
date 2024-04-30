package com.yeung.seckill.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * @author Yeung Law~
 * <p>
 * 对服务端返回用户信息的枚举
 */

@Getter
@ToString
@AllArgsConstructor
public enum RespBeanEnum {
    //通用信息
    SUCCESS(200, "SUCCESS"),

    ERROR(500, "服务端异常"),

    //登录信息
    LOGIN_ERROR(500200, "用户id或密码错误"),
    BING_ERROR(500210, "参数绑定异常"),
    MOBILE_ERROR(500211, "手机号码格式不正确"),
    MOBILE_NOT_EXIST(500213, "手机号码不存在"),
    PASSWORD_UPDATE_FAIL(500215, "密码更新失败"),

    //秒杀信息
    ENTRY_STOCK(500500, "库存不足"),
    REPEAT_ERROR(500501, "该商品仅限购一件"),
    //请求信息
    REQUEST_ILLEGAL(500502, "请求非法"),
    SESSION_ERROR(500503, "用户信息有误"),
    SEC_KILL_WAIT(500505, "排队中..."),

    // 验证码校验失败
    CAPTCHA_ERROR(500506, "验证码错误"),
    ACCESS_LIMIT_REACHED(500508, "访问频繁，请待会再试..."),
    SEC_KILL_RETRY(500511, "抢购失败，请待会再试...");


    private final Integer code;
    private final String message;

}
