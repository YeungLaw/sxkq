package com.yeung.seckill.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yeung.seckill.exception.GlobalException;
import com.yeung.seckill.mapper.UserMapper;
import com.yeung.seckill.pojo.User;
import com.yeung.seckill.service.UserService;
import com.yeung.seckill.util.CookieUtil;
import com.yeung.seckill.util.MD5Util;
import com.yeung.seckill.util.UUIDUtil;

import com.yeung.seckill.vo.LoginVo;
import com.yeung.seckill.vo.RespBean;
import com.yeung.seckill.vo.RespBeanEnum;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Yeung Law~
 */

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public RespBean doLogin(LoginVo loginVo, HttpServletRequest request, HttpServletResponse response) {

        // 先根据登录信息接收 mobile 和 password
        String mobile = loginVo.getMobile();
        String password = loginVo.getPassword();

        // 将格式校验问题 通过 注解验证以及全局异常处理器实现
        // //1.校验 手机号码 与 密码 是否不为空
        // if (!StringUtils.hasText(mobile) || !StringUtils.hasText(password)) {
        //     return RespBean.error(RespBeanEnum.LOGIN_ERROR);
        // }
        //
        // //2.校验 手机号码 是否符合规则
        // if (!ValidatorUtil.isMobile(mobile)) {
        //     return RespBean.error(RespBeanEnum.MOBILE_ERROR);
        // }

        //查询数据库 核对用户信息
        User user = userMapper.selectById(mobile);
        if (null == user) {
            // return RespBean.error(RespBeanEnum.MOBILE_NOT_EXIST);
            throw new GlobalException(RespBeanEnum.MOBILE_NOT_EXIST);
        }
        //用工具类拿到最终的数据库密码
        String inputPass = MD5Util.midPassToDBPass(password, user.getSlat());

        if (!inputPass.equals(user.getPassword())) {
            return RespBean.error(RespBeanEnum.LOGIN_ERROR);

        }else {
            //用户登录成功，给每个用户生成票据
            // 方案一：将登录成功的用户信息以session的形式保存到redis
            // request.getSession().setAttribute(ticket,user);

            // 方案二：直接将整个用户信息保存到redis中进行缓存
            // key：user:{ticket}   value: {user}

            String ticket = UUIDUtil.uuid();
            String key = "user:" + ticket;
            redisTemplate.opsForValue().set(key, user);

            //将ticket保存到cookie
            CookieUtil.setCookie(request,response,"userTicket", ticket);

            // 新增一个票据返回
            return RespBean.success(ticket);
        }

    }

    /**
     * 从redis缓存中获取用户的 cookie值
     * @param userTicket
     * @param request
     * @param response
     * @return
     */
    @Override
    public User getUserByCookie(String userTicket, HttpServletRequest request,
                                HttpServletResponse response) {
        if (!StringUtils.hasText(userTicket)) {
            return null;
        }
        User user = (User)redisTemplate.opsForValue().get("user:" + userTicket);

        // 刷新cookie值
        if (user != null) {
            CookieUtil.setCookie(request, response, "userTicket", userTicket);
        }
        return user;
    }

    /**
     * 更新密码， 需要同步更新 redis中的用户数据
     * @param userTicket
     * @param password
     * @param request
     * @param response
     * @return
     */
    @Override
    public RespBean updatePassword(String userTicket, String password,
                                   HttpServletRequest request, HttpServletResponse response) {

        User user = getUserByCookie(userTicket, request, response);
        if (user == null) {
            throw new GlobalException(RespBeanEnum.MOBILE_NOT_EXIST);
        }
        user.setPassword(MD5Util.inputPassToDBPass(password, user.getSlat()));
        // 将用户更新到DB
        int row = userMapper.updateById(user);
        if (row == 1) {
            // 更新成功，再从redis中删除数据
            redisTemplate.delete("user:" + userTicket);
            return RespBean.success();
        }
        // 设置新的密码
        return RespBean.error(RespBeanEnum.PASSWORD_UPDATE_FAIL);
    }
}
