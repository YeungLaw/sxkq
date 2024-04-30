package com.yeung.seckill.vo;

import com.yeung.seckill.validator.IsMobile;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author Yeung Law~
 *
 * 用来接收用户登录时发送的身份信息
 * 包括 手机号和登录密码
 *
 * Value Object，有的也称为View Object，即值对象或页面对象。
 * 一般用于web层向view层封装并提供需要展现的数据。
 */
@Data
public class LoginVo {

    @NotNull
    @IsMobile()
    private String mobile;

    @NotNull
    private String password;
}
