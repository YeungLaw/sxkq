package com.yeung.seckill.rabbtimq;

import cn.hutool.json.JSONUtil;
import com.yeung.seckill.pojo.Order;
import com.yeung.seckill.pojo.SeckillMessage;
import com.yeung.seckill.pojo.User;
import com.yeung.seckill.service.GoodsService;
import com.yeung.seckill.service.OrderService;
import com.yeung.seckill.vo.GoodsVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author Yeung Law~
 *
 * MQMessageConsumer 秒杀消息的消费者
 */
@Service
@Slf4j
public class MQMessageConsumer {

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource
    private GoodsService goodsService;

    @Resource
    private OrderService orderService;

    // 接收秒杀消息,并完成秒杀下单
    @RabbitListener(queues = "seckillQueue")
    public void queue(String msg) {
        //
        log.info("接收的消息-->" + msg);
        // 引入hutool依赖 将msg反序列化 为SeckillMessage对象
        SeckillMessage seckillMessage = JSONUtil.toBean(msg, SeckillMessage.class);
        User user = seckillMessage.getUser();

        // 通过商品id 获取对应的goodsVo
        Long goodsId = seckillMessage.getGoodsId();
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);

        orderService.seckill(user, goodsVo);
    }
}
