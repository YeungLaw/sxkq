package com.yeung.seckill.controller;

import com.yeung.seckill.service.UserService;
import com.yeung.seckill.vo.LoginVo;
import com.yeung.seckill.vo.RespBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;


/**
 * @author Yeung Law~
 */

@Controller
@RequestMapping("/login")
@Slf4j

public class LoginController {

    @Resource
    private UserService userService;

    @RequestMapping("/toLogin")
    public String toLogin() {

        return "login";
        // 视图解析将会解析到templates/login.html
    }

    @RequestMapping("/doLogin")
    @ResponseBody
    public RespBean doLogin(@Valid LoginVo loginVo,
                            HttpServletRequest request,
                            HttpServletResponse response) {
        log.info("{}" , loginVo);
        // 登录：根据数据库信息验证用户密码
        RespBean respBean = userService.doLogin(loginVo, request, response);
        return respBean;
    }

}
