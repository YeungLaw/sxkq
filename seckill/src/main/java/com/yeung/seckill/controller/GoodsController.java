package com.yeung.seckill.controller;

import com.yeung.seckill.pojo.User;
import com.yeung.seckill.service.GoodsService;
import com.yeung.seckill.service.UserService;
import com.yeung.seckill.vo.GoodsVo;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author Yeung Law~
 */

@Controller
@RequestMapping("/goods")
public class GoodsController {

    @Resource
    private UserService userService;

    @Resource
    private GoodsService goodsService;

    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 手动渲染需要的模板解析器
     */
    @Resource
    private ThymeleafViewResolver thymeleafViewResolver;

    /**
     * v1.0
     */
    // @RequestMapping("toList")
    // public String toList(HttpSession session, Model model,
    //                      @CookieValue("userTicket") String ticket) {
    //     if (!StringUtils.hasText(ticket)) {
    //         return "login";
    //     }
    //     User user = (User)session.getAttribute(ticket);
    //     if (null == user) {
    //         return "login";
    //     }
    //
    //     model.addAttribute("user",user);
    //     return "goodsList";
    // }

    /**
     * v2.0
     */
    // @RequestMapping("toList")
    // public String toList(Model model,
    //                      @CookieValue("userTicket") String ticket,
    //                      HttpServletRequest request, HttpServletResponse response) {
    //     if (!StringUtils.hasText(ticket)) {
    //         return "login";
    //     }
    //
    //     // 信息已经保存到redis，因此需要去redis获取信息
    //     User user = userService.getUserByCookie(ticket, request, response);
    //     if (user == null) {
    //         return "login";
    //     }
    //     model.addAttribute("user",user);
    //     return "goodsList";
    // }

    /**
     * v3.0
     * 到DB查询商品列表
     * @param model
     * @param user
     * @return
     */
    // @RequestMapping("toList")
    // public String toList(Model model,User user) {
    //     // if (!StringUtils.hasText(ticket)) {
    //     //     return "login";
    //     // }
    //     //
    //     // // 信息已经保存到redis，因此需要去redis获取信息
    //     // User user = userService.getUserByCookie(ticket, request, response);
    //     if (null == user) {
    //         return "login";
    //     }
    //     model.addAttribute("user",user);
    //
    //     model.addAttribute("goodsList", goodsService.findGoodsVo());
    //     return "goodsList";
    // }

    /**
     * v4.0
     * 将商品列表缓存到redis中
     *
     * @param model 模型
     * @param user
     * @return
     */
    @RequestMapping(value = "toList", produces = "text/html;charset=utf-8")
    @ResponseBody
    public String toList(Model model, User user,
                         HttpServletRequest request, HttpServletResponse response) {
        // if (!StringUtils.hasText(ticket)) {
        //     return "login";
        // }
        //
        // // 信息已经保存到redis，因此需要去redis获取信息
        // User user = userService.getUserByCookie(ticket, request, response);

        // 登录成功后，会将user用户保存到cookie中，否则就需要进行登录
        if (null == user) {
            return "login";
        }

        // 先尝试从redis中获取页面
        ValueOperations valueOperations = redisTemplate.opsForValue();
        String html = (String) valueOperations.get("goodsList");

        if (StringUtils.hasText(html)) {
            return html;
        }
        // 不是为了返回给用户端，而且方便后端操作
        model.addAttribute("user", user);

        model.addAttribute("goodsList", goodsService.findGoodsVo());
        // 从redis没有获取到页面，手动渲染，并存入到redis
        WebContext webContext =
                new WebContext(request, response, request.getServletContext(),
                        request.getLocale(), model.asMap());
        // public abstract String process(String viewName, org.thymeleaf.context.IContext iContext)
        // viewName参数是需要渲染的视图名，iContext是渲染需要用的配置类
        html = thymeleafViewResolver.getTemplateEngine().process("goodsList", webContext);
        if (StringUtils.hasText(html)) {
            // 将页面保存到redis，并且每隔60s 更新一次
            valueOperations.set("goodsList", html, 60, TimeUnit.SECONDS);
        }
        return html;
    }


    /**
     * v1.0
     * 从DB中查询数据信息
     */
    // @RequestMapping("/toDetail/{goodsId}")
    // public String toDetail(Model model, User user, @PathVariable Long goodsId) {
    //     if (user == null) {
    //         return "login";
    //     }
    //     GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
    //     model.addAttribute("goods", goodsVo);
    //
    //     // 对秒杀的状态和剩余时间进行处理
    //     // 变量 seckillStatus 秒杀状态 0：未开始， 1：进行中， 2：已结束
    //     // 变量 remainSeconds > 0 剩余秒杀还有多久开始 -1：秒杀结束
    //     int seckillStatus = 0;
    //     int remainSeconds = 0;
    //
    //     Date startDate = goodsVo.getStartDate();
    //     Date endDate = goodsVo.getEndDate();
    //     Date nowDate = new Date();
    //
    //     if (nowDate.before(startDate)) {
    //         //
    //         remainSeconds = (int)(startDate.getTime() - nowDate.getTime()) / 1000;
    //     } else if (nowDate.after(endDate)) {
    //         seckillStatus = 2;
    //         remainSeconds = -1;
    //     }else {
    //         // 正在秒杀中
    //         seckillStatus = 1;
    //         remainSeconds = 0;
    //     }
    //
    //     model.addAttribute("secKillStatus",seckillStatus);
    //     model.addAttribute("remainSeconds",remainSeconds);
    //
    //     return "goodsDetail";
    // }

    /**
     * v2.0
     * 从redis中查询数据信息
     */
    @RequestMapping(value = "/toDetail/{goodsId}", produces = "text/html;charset=utf-8")
    @ResponseBody
    public String toDetail(Model model, User user, @PathVariable Long goodsId,
                           HttpServletRequest request, HttpServletResponse response) {
        if (user == null) {
            return "login";
        }

        ValueOperations valueOperations = redisTemplate.opsForValue();
        String html = (String) valueOperations.get("goodsDetail:" + goodsId);

        if (StringUtils.hasText(html)) {
            return html;
        }

        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
        model.addAttribute("goods", goodsVo);

        // 对秒杀的状态和剩余时间进行处理
        // 变量 seckillStatus 秒杀状态 0：未开始， 1：进行中， 2：已结束
        // 变量 remainSeconds > 0 剩余秒杀还有多久开始 -1：秒杀结束
        int seckillStatus = 0;
        int remainSeconds = 0;

        Date startDate = goodsVo.getStartDate();
        Date endDate = goodsVo.getEndDate();
        Date nowDate = new Date();

        if (nowDate.before(startDate)) {
            //
            remainSeconds = (int) (startDate.getTime() - nowDate.getTime()) / 1000;
        } else if (nowDate.after(endDate)) {
            seckillStatus = 2;
            remainSeconds = -1;
        } else {
            // 正在秒杀中
            seckillStatus = 1;
            remainSeconds = 0;
        }

        model.addAttribute("secKillStatus", seckillStatus);
        model.addAttribute("remainSeconds", remainSeconds);

        // 如果没有从redis获取到详情页，需要将其进行手动渲染，再返回
        // 需要注意的是：渲染要在model设置之后
        WebContext webContext =
                new WebContext(request, response, request.getServletContext(),
                        request.getLocale(), model.asMap());

        html = thymeleafViewResolver.getTemplateEngine().process("goodsDetail", webContext);

        if (StringUtils.hasText(html)) {
            // 将页面保存到redis，并且每隔60s 更新一次
            valueOperations.set("goodsDetail:" + goodsId, html, 60, TimeUnit.SECONDS);
        }

        return html;
    }


}
