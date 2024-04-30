package com.yeung.seckill.controller;

import com.yeung.seckill.pojo.User;
import com.yeung.seckill.service.UserService;
import com.yeung.seckill.vo.RespBean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Yeung Law~
 *
 * 用于jmeter压力测试
 */

@Controller
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    @ResponseBody
    @RequestMapping("/info")
    public RespBean info(User user, String address) {
        return RespBean.success(user);
    }

    @RequestMapping("/updpwd")
    @ResponseBody
    private RespBean updatePassword(String userTicket, String password,
                                    HttpServletRequest request, HttpServletResponse response) {

        return userService.updatePassword(userTicket, password, request, response);
    }
}
