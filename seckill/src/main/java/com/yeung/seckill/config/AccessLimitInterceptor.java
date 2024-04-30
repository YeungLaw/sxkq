package com.yeung.seckill.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yeung.seckill.pojo.User;
import com.yeung.seckill.service.UserService;
import com.yeung.seckill.util.CookieUtil;
import com.yeung.seckill.validator.AccessLimit;
import com.yeung.seckill.vo.RespBean;
import com.yeung.seckill.vo.RespBeanEnum;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * @author Yeung Law~
 *
 * 自定义的拦截器
 */

@Component
public class AccessLimitInterceptor implements HandlerInterceptor {

    @Resource
    private UserService userService;
    @Resource
    private RedisTemplate redisTemplate;


    /**
     * 封装user对象，并加入到ThreadLocal中
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if (handler instanceof HandlerMethod) {
            // 将user加入到线程共享数据池 ThreadLocal中
            User user = getUser(request, response);
            UserContext.setUser(user);

            HandlerMethod hm = (HandlerMethod)handler;
            AccessLimit accessLimit = hm.getMethodAnnotation(AccessLimit.class);
            if (accessLimit == null) {
                // 请求的hander未被@AccessLimit注解，因此不处理限流防刷操作
                return true;
            }
            int maxCount = accessLimit.maxCount();
            int secend = accessLimit.secend();
            boolean needLogin = accessLimit.needLogin();

            if (needLogin) {
                if (user == null) {
                    // goto
                    render(response,RespBeanEnum.SESSION_ERROR);
                    return false;
                }
            }
            String uri = request.getRequestURI();
            ValueOperations valueOperations = redisTemplate.opsForValue();
            String key = uri + ":" + user.getId();

            Integer count = (Integer)valueOperations.get(key);
            if (count == null) {
                valueOperations.set(key,1,secend, TimeUnit.SECONDS);
            }else if (count < maxCount) {
                valueOperations.increment(key);
            }else {
                // 判定为刷接口
                render(response,RespBeanEnum.ACCESS_LIMIT_REACHED);
                return false;
            }
        }

        return true;
    }

    private User getUser(HttpServletRequest request, HttpServletResponse response) {
        // 从request域中获取cookie值（需要进行反序列化），通过封装的工具实现
        String ticket = CookieUtil.getCookieValue(request, "userTicket");
        if (!StringUtils.hasText(ticket)) {
            return null;
        }
        // 从cookie中拿到的ticket 再根据ticket从Redis缓存中来获取用户信息
        return userService.getUserByCookie(ticket, request, response);
    }

    private void render(HttpServletResponse response, RespBeanEnum respBeanEnum) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        PrintWriter out = response.getWriter();

        // 构建并返回RespBean对象
        RespBean error = RespBean.error(respBeanEnum);
        out.write(new ObjectMapper().writeValueAsString(error));
        out.flush();
        out.close();
    }
}
