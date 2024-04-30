package com.yeung.seckill.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yeung.seckill.mapper.OrderMapper;
import com.yeung.seckill.pojo.Order;
import com.yeung.seckill.pojo.SeckillGoods;
import com.yeung.seckill.pojo.SeckillOrder;
import com.yeung.seckill.pojo.User;
import com.yeung.seckill.service.OrderService;
import com.yeung.seckill.service.SeckillGoodsService;
import com.yeung.seckill.service.SeckillOrderService;
import com.yeung.seckill.util.MD5Util;
import com.yeung.seckill.util.UUIDUtil;
import com.yeung.seckill.vo.GoodsVo;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author Yeung Law~
 */

@Service
public class OrderServiceImpl
        extends ServiceImpl<OrderMapper, Order> implements OrderService {

    @Resource
    private OrderMapper orderMapper;

    @Resource
    private SeckillGoodsService seckillGoodsService;

    @Resource
    private SeckillOrderService seckillOrderService;

    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 默认已通过 重购，库存余量验证，开始进行秒杀
     * @param user 用户信息
     * @param goodsVo 商品信息
     * @return 返回生成的订单信息 或 失败报错信息
     */
    @Override
    @Transactional
    public Order seckill(User user, GoodsVo goodsVo) {
        // 先根据用户和商品信息 生成普通订单
        // 再生成秒杀订单
        // 1.1 查询库存并减一
        SeckillGoods seckillGoods = seckillGoodsService.getOne(
                new QueryWrapper<SeckillGoods>().eq("goods_id", goodsVo.getId()));
        // 秒杀操作v1.0 [没有实现库存更新原子性]，后续在高并发的情况下再进行进一步优化
        // seckillGoods.setStockCount(seckillGoods.getStockCount() - 1);
        // seckillGoodsService.updateById(seckillGoods);

        // 秒杀操作v2.0 实现原子性
        // 使用@Transactional注解，在Mysql默认的事务隔离级别【REPEATABLE-READ】下
        // 执行update操作时， 事务会锁定当前操作行
        // 可以防止同一时间有其他会话执行update，delete操作
        // 存在的问题：高并发时，对DB造成很大的压力
        // 解决方案：在redis中判断预减库存
        // 最终还是要操作数据库
        boolean update = seckillGoodsService.update(new UpdateWrapper<SeckillGoods>()
                .setSql("stock_count=stock_count-1")
                .eq("goods_id", goodsVo.getId()).gt("stock_count", 0));
        if (!update) {
            return null;
        }
        // 1.2 生成普通订单
        Order order = new Order();
        order.setUserId(user.getId());
        order.setGoodsId(goodsVo.getId());
        order.setDeliveryAddrId(0L);
        order.setGoodsName(goodsVo.getGoodsName());
        order.setGoodsCount(1);
        order.setGoodsPrice(seckillGoods.getSeckillPrice());
        order.setOrderChannel(1);
        order.setStatus(0);
        order.setCreateDate(new Date());

        orderMapper.insert(order);

        // 1.3 根据普通订单，生成秒杀订单
        SeckillOrder seckillOrder = new SeckillOrder();

        seckillOrder.setGoodsId(goodsVo.getId());
        seckillOrder.setOrderId(order.getId());
        seckillOrder.setUserId(user.getId());

        seckillOrderService.save(seckillOrder);

        // 进一步优化：将生成的秒杀订单更新到redis缓存中
        // key值设计—— order:user_id:goods_id
        redisTemplate.opsForValue()
                .set("order:"+user.getId() + ":" + goodsVo.getId(), seckillOrder);

        return order;
    }

    @Override
    public String createPath(User user, Long goodsId) {
        // 生成唯一秒杀路径值
        String path = MD5Util.md5(UUIDUtil.uuid());

        // 将随机路径值存入到redis中
        // key的设计：seckillPath:userId:goodsId
        redisTemplate.opsForValue().set("seckillPath:"+ user.getId() + ":"
                + goodsId,path,60, TimeUnit.SECONDS);
        return path;
    }

    @Override
    public boolean checkPath(User user, Long goodsId, String path) {

        if (user == null || goodsId < 0 || !StringUtils.hasText(path)) {
            return false;
        }
        String redisPath = (String)redisTemplate.opsForValue()
                .get("seckillPath:" + user.getId() + ":" + goodsId);

        return path.equals(redisPath);
    }

    @Override
    public boolean checkCaptcha(User user, Long goodsId, String captcha) {
        if (user == null || goodsId < 0 || !StringUtils.hasText(captcha)) {
            return false;
        }
        // 从redis中取出并验证
        String redisCaptcha = (String)redisTemplate.opsForValue()
                .get("captcha:" + user.getId() + ":" + goodsId);
        return captcha.equals(redisCaptcha);
    }
}
