package com.yeung.seckill.rabbtimq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author Yeung Law~
 *
 * 消息的生产者
 */

@Service
@Slf4j
public class MQSender {

    // 装备RabbitTemplate->操作RabbitMQ
    @Resource
    private RabbitTemplate rabbitTemplate;

    // 方法：发生消息
    public void send(Object msg) {
        log.info("发生消息：" + msg);
        rabbitTemplate.convertAndSend("queue",msg);
    }

    // 发生消息到交换机
    public void sendFanout(Object msg) {
        log.info("发生消息：" + msg);
        // convertAndSend(String exchange, String routingKey, Object object)
        // 忽略路由
        rabbitTemplate.convertAndSend("fanoutExchange","",msg);
    }

    // 发送消息到direct交换机，同时指定路由
    public void sendDirect01(Object msg) {
        log.info("发生消息：" + msg);
        // convertAndSend(String exchange, String routingKey, Object object)
        // 忽略路由
        rabbitTemplate.convertAndSend("directExchange", "queue.red", msg);
    }

    public void sendDirect02(Object msg) {
        log.info("发生消息：" + msg);
        // convertAndSend(String exchange, String routingKey, Object object)
        // 忽略路由
        rabbitTemplate.convertAndSend("directExchange", "queue.green", msg);
    }
}
