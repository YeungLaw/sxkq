package com.yeung.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yeung.seckill.pojo.User;
import com.yeung.seckill.vo.LoginVo;
import com.yeung.seckill.vo.RespBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Yeung Law~
 * <p>
 * 相比于传统方法，在mybatis-plus中基础IService 更加方便
 */
public interface UserService extends IService<User> {

    // 完成用户登录校验
    RespBean doLogin(LoginVo loginVo, HttpServletRequest request,
                     HttpServletResponse response);

    User getUserByCookie(String userTicket, HttpServletRequest request,
                         HttpServletResponse response);

    // 更新密码
    RespBean updatePassword(String userTicket, String password,
                            HttpServletRequest request, HttpServletResponse response);
}
