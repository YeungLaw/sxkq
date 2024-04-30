package com.yeung.seckill.controller;

import cn.hutool.json.JSONUtil;
import com.ramostear.captcha.HappyCaptcha;
import com.ramostear.captcha.common.Fonts;
import com.ramostear.captcha.support.CaptchaStyle;
import com.ramostear.captcha.support.CaptchaType;
import com.yeung.seckill.pojo.Order;
import com.yeung.seckill.pojo.SeckillMessage;
import com.yeung.seckill.pojo.SeckillOrder;
import com.yeung.seckill.pojo.User;
import com.yeung.seckill.rabbtimq.MQMessageProducer;
import com.yeung.seckill.rabbtimq.MQSender;
import com.yeung.seckill.service.GoodsService;
import com.yeung.seckill.service.OrderService;
import com.yeung.seckill.service.SeckillOrderService;
import com.yeung.seckill.util.UUIDUtil;
import com.yeung.seckill.validator.AccessLimit;
import com.yeung.seckill.vo.GoodsVo;
import com.yeung.seckill.vo.RespBean;
import com.yeung.seckill.vo.RespBeanEnum;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Yeung Law~
 *
 * InitializingBean接口 有个方法
 * public void afterPropertiesSet() 在类的所有属性初始化后，自动执行
 */

@Controller
@RequestMapping("/seckill")
public class SeckillController implements InitializingBean {

    @Resource
    private GoodsService goodsService;

    @Resource
    private SeckillOrderService seckillOrderService;

    @Resource
    private OrderService orderService;

    @Resource
    private RedisTemplate redisTemplate;

    // 执行lua脚本的类对象
    @Resource
    private RedisScript<Long> script;

    /**
     * 向消息队列发送秒杀信息，实现秒杀异步请求
     */
    @Resource
    private MQMessageProducer mqMessageProducer;

    /**
     * 定义map 记录redis中缓存的商品是否还有库存
     * false表示还有库存
     */
    private HashMap<Long, Boolean> entryStockMap = new HashMap<>();


    // 先实现 v1.0版本，再进一步优化
    // public String doSeckill(Model model, User user, Long goodsId){
    //
    //     System.out.println("▛--------秒杀v1.0--------▜");
    //
    //     if (user == null) {
    //         return "login";
    //     }
    //     model.addAttribute("user", user);
    //     // 根据商品id 获取商品详细信息
    //     GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
    //
    //     // 判断库存
    //     Integer goodsStock = goodsVo.getGoodsStock();
    //     if (goodsStock < 1) {
    //         // 没有库存，不能买了
    //         model.addAttribute("errmsg", RespBeanEnum.ENTRY_STOCK.getMessage());
    //         // 返回错误提示页面
    //         return "seckillFail";
    //     }
    //     // 复购判断 v1.0
    //     // 通过用户user_id和商品goods_id查询秒杀订单表t_seckill_order
    //     SeckillOrder seckillOrder = seckillOrderService.getOne(
    //             new QueryWrapper<SeckillOrder>()
    //                     .eq("user_id", user.getId()).eq("goods_id", goodsId));
    //     if (seckillOrder != null) {
    //         // 发生复购
    //         model.addAttribute("errmsg",RespBeanEnum.REPEAT_ERROR.getMessage());
    //         return "seckillFail";
    //     }
    //
    //
    //     // 最后进行秒杀
    //     Order order = orderService.seckill(user, goodsVo);
    //     if (order == null) {
    //         // 下单发生错误
    //         model.addAttribute("errmsg", "下单发生异常，秒杀失败");
    //         return "seckillFail";
    //     }
    //     // 进入订单详情页
    //     model.addAttribute("order", order);
    //     model.addAttribute("goods", goodsVo);
    //
    //     System.out.println("▙--------秒杀v1.0--------▟");
    //     return "orderDetail";
    // }

    // 先实现 v2.0版本，再进一步优化
    // public String doSeckill(Model model, User user, Long goodsId) {
    //
    //     // v1.0------- 根据商品id 从DB中获取商品详细信息（可以优化）
    //     GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
    //     // Integer goodsStock = goodsVo.getGoodsStock();
    //
    //     // v2.0 根据商品id 从redis中获取并预减商品库存，对应的key为：seckillGoods:商品id
    //     // decrement()方法具有原子性
    //     Long goodsStock = redisTemplate.opsForValue().decrement("seckillGoods:" + goodsId);
    //
    //     // 判断库存
    //     if (goodsStock < 0) {
    //         // 没有库存，不能买了
    //         // 恢复库存为0
    //         redisTemplate.opsForValue().increment("seckillGoods:" + goodsId);
    //         model.addAttribute("errmsg", RespBeanEnum.ENTRY_STOCK.getMessage());
    //         // 返回错误提示页面
    //         return "seckillFail";
    //     }
    //
    //     // 复购判断 v2.0 从redis中获取是否复购验证
    //     SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.opsForValue()
    //             .get("order:" + user.getId() + ":" + goodsId);
    //     if (null != seckillOrder) {
    //         // 不为空，说明发生复购
    //         model.addAttribute("errmsg", RespBeanEnum.REPEAT_ERROR.getMessage());
    //         return "seckillFail";
    //
    //     }
    //
    //     // 最后进行秒杀
    //     Order order = orderService.seckill(user, goodsVo);
    //     return "orderDetail";
    // }


    // //  v3.0版本
    // // 添加一个map，作为redis之前的缓存
    // @RequestMapping("/doSeckill")
    // public String doSeckill(Model model, User user, Long goodsId) {
    //
    //     // v3.0 根据【内存标记】判断map中商品是否还有库存
    //     if (entryStockMap.get(goodsId)) {
    //         model.addAttribute("errmsg", RespBeanEnum.ENTRY_STOCK.getMessage());
    //         // 返回错误提示页面
    //         return "seckillFail";
    //     }
    //
    //     // v2.0 根据商品id 从redis中获取并预减商品库存，对应的key为：seckillGoods:商品id
    //     // decrement()方法具有原子性
    //     Long goodsStock = redisTemplate.opsForValue().decrement("seckillGoods:" + goodsId);
    //
    //     // 判断库存
    //
    //     // 复购判断 v2.0 从redis中获取是否复购验证
    //
    //     // 最后进行秒杀
    //     return "orderDetail";
    // }

    // //  v4.0版本
    // // 引入消息队列，实现秒杀的异步请求
    // @RequestMapping("/doSeckill")
    // public String doSeckill(Model model, User user, Long goodsId) {
    //
    //     if (user == null) {
    //         return "login";
    //     }
    //     // v1.0------- 根据商品id 从DB中获取商品详细信息（可以优化）
    //     GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
    //
    //     // v3.0 根据【内存标记】判断map中商品是否还有库存
    //     if (entryStockMap.get(goodsId)) {
    //         model.addAttribute("errmsg", RespBeanEnum.ENTRY_STOCK.getMessage());
    //         // 返回错误提示页面
    //         return "seckillFail";
    //     }
    //
    //     // 复购判断 v2.0 从redis中获取是否复购验证
    //     SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.opsForValue()
    //             .get("order:" + user.getId() + ":" + goodsId);
    //     if (null != seckillOrder) {
    //         // 不为空，说明发生复购
    //         model.addAttribute("errmsg", RespBeanEnum.REPEAT_ERROR.getMessage());
    //         return "seckillFail";
    //
    //     }
    //
    //     // v2.0 根据商品id 从redis中获取并预减商品库存，对应的key为：seckillGoods:商品id
    //     // decrement()方法具有原子性
    //     Long goodsStock = redisTemplate.opsForValue().decrement("seckillGoods:" + goodsId);
    //
    //     // 判断库存
    //
    //     if (goodsStock < 0) {
    //         // 没有库存，不能买了
    //         // 恢复库存为0
    //         entryStockMap.put(goodsId,true);
    //         redisTemplate.opsForValue().increment("seckillGoods:" + goodsId);
    //         model.addAttribute("errmsg", RespBeanEnum.ENTRY_STOCK.getMessage());
    //         // 返回错误提示页面
    //         return "seckillFail";
    //     }
    //
    //
    //     // 最后进行秒杀
    //     // v4.0 向消息队列实现秒杀异步请求
    //     SeckillMessage seckillMessage = new SeckillMessage(user, goodsId);
    //     mqMessageProducer.sendSeckillMessage(JSONUtil.toJsonStr(seckillMessage));
    //
    //     model.addAttribute("errmsg", "秒杀排队中");
    //     return "seckillFail";
    // }

    //  v5.0版本
    // 加入秒杀安全机制，动态生成秒杀路径
    @RequestMapping("/{path}/doSeckill")
    @ResponseBody
    public RespBean doSeckill(User user, Long goodsId,
                              @PathVariable String path) {

        if (user == null) {
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }

        // 增加一个判断逻辑，校验用户携带的路径是否正确
        boolean bool = orderService.checkPath(user, goodsId, path);
        if (!bool) {
            return RespBean.error(RespBeanEnum.REQUEST_ILLEGAL);
        }
        // 根据商品id 从DB中获取商品详细信息（可以优化）
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);

        if (goodsVo.getStockCount() < 1) {
            return RespBean.error(RespBeanEnum.ENTRY_STOCK);
        }

        // 库存判断 根据【内存标记】判断map中商品是否还有库存(初始化时存入内存中)
        if (entryStockMap.get(goodsId)) {
            return RespBean.error(RespBeanEnum.ENTRY_STOCK);
        }

        // 复购判断 从redis中获取是否复购验证
        SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.opsForValue()
                .get("order:" + user.getId() + ":" + goodsId);
        if (null != seckillOrder) {
            // 不为空，说明发生复购
            return RespBean.error(RespBeanEnum.REPEAT_ERROR);

        }

        // 根据商品id 从redis中获取并预减商品库存，对应的key为：seckillGoods:商品id
        // decrement()方法具有原子性，也可以使用【脚本】进行单线程删除，以实现其原子性

        String uuid = UUIDUtil.uuid();

        // String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del',KEYS[1]) else return 0 end";
        // DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        // redisScript.setScriptText(script);
        // redisScript.setResultType(Long.class);

        // 获取锁并设置过期时间以防止死锁
        Boolean lock = redisTemplate.opsForValue().setIfAbsent("lock", uuid, 3, TimeUnit.SECONDS);

        if (lock) {
            // 执行库存预减操作
            Long decrement = redisTemplate.opsForValue().decrement("seckillGoods:" + goodsId);

            if (decrement < 0) {
                // 没有库存，不能买了
                // 恢复内存标记中 该商品的库存为0
                entryStockMap.put(goodsId, true);
                redisTemplate.opsForValue().increment("seckillGoods:" + goodsId);

                // 执行lua脚本，释放锁
                redisTemplate.execute(script, Arrays.asList("lock"), uuid);
                // 返回错误提示页面
                return RespBean.error(RespBeanEnum.ENTRY_STOCK);
            }

            // 执行lua脚本释放锁，以保证删除的原子性
            redisTemplate.execute(script, Arrays.asList("lock"), uuid);

        } else {
            // 获取锁失败，返回本次抢购失败，请再次抢购
            return RespBean.error(RespBeanEnum.SEC_KILL_RETRY);
        }

        // 向消息队列实现秒杀异步请求
        SeckillMessage seckillMessage = new SeckillMessage(user, goodsId);
        mqMessageProducer.sendSeckillMessage(JSONUtil.toJsonStr(seckillMessage));
        return RespBean.error(RespBeanEnum.SEC_KILL_WAIT);
    }

    /**
     * 获取秒杀路径
     */
    @RequestMapping("/path")
    @ResponseBody
    @AccessLimit(secend = 5, maxCount = 5, needLogin = true)
    public RespBean getPath(User user, Long goodsId, String captcha,
                            HttpServletRequest request) {
        if (user == null || goodsId < 0 || !StringUtils.hasText(captcha)) {
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }

        // 增加一个业务逻辑：完成对用户的防刷线路
        // 例如在5s内访问次数超过了5次，则被判定为刷接口

        // 以下代码封装到注解中实现
        // uri = /seckill/path
        // String uri = request.getRequestURI();
        // ValueOperations valueOperations = redisTemplate.opsForValue();
        // String key = uri + ":" + user.getId();
        //
        // Integer count = (Integer)valueOperations.get(key);
        // if (count == null) {
        //     valueOperations.set(key,1,5,TimeUnit.SECONDS);
        // }else if (count < 5) {
        //     valueOperations.increment(key);
        // }else {
        //     // 判定为刷接口
        //     return RespBean.error(RespBeanEnum.ACCESS_LIMIT_REACHED);
        // }


        // 增加一个业务逻辑，校验用户输入的验证
        boolean check = orderService.checkCaptcha(user, goodsId, captcha);
        if (!check) {
            // 校验失败，返回验证码不正确
            return RespBean.error(RespBeanEnum.CAPTCHA_ERROR);
        }
        String path = orderService.createPath(user, goodsId);
        return RespBean.success(path);
    }

    @RequestMapping("/checkCaptcha")
    @ResponseBody
    public RespBean checkCaptcha(User user, Long goodsId, String captcha) {
        if (user == null || goodsId < 0 || !StringUtils.hasText(captcha)) {
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        // 增加一个业务逻辑，校验用户输入的验证
        boolean check = orderService.checkCaptcha(user, goodsId, captcha);
        if (!check) {
            // 校验失败，返回验证码不正确
            return RespBean.error(RespBeanEnum.CAPTCHA_ERROR);
        }
        return RespBean.success();
    }


    /**
     * afterPropertiesSet() 在类的所有属性初始化后，自动执行
     * 用来将秒杀商品预装载到redis中
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> goodsVos = goodsService.findGoodsVo();
        if (CollectionUtils.isEmpty(goodsVos)) {
            return;
        }
        // 秒杀商品库存量对应的key为：seckillGoods:商品id
        goodsVos.forEach((goodsVo -> {

            redisTemplate.opsForValue().set("seckillGoods:" + goodsVo.getId(), goodsVo.getStockCount());
            // 初始化map,false表示还有库存
            entryStockMap.put(goodsVo.getId(), false);
        }));
    }


    // 生成验证码-happy_captcha
    @RequestMapping("/captcha")
    public void happyCaptcha(HttpServletRequest request, HttpServletResponse response, User user, Long goodsId) {
        // 生成并输出验证码
        // 验证码同时保存在session中，key是 happy-captcha
        HappyCaptcha.require(request, response)
                .style(CaptchaStyle.ANIM)
                .type(CaptchaType.NUMBER)
                .length(6)
                .width(220)
                .height(80)
                .font(Fonts.getInstance().zhFont())
                .build().finish();
        // 将验证码同步保存到redis中[考虑到项目分布式，不同的机器不能拿到用一个session]
        redisTemplate.opsForValue()
                .set("captcha:" + user.getId() + ":" + goodsId,
                        (String) request.getSession().getAttribute("happy-captcha"), 100, TimeUnit.SECONDS);
    }
}
