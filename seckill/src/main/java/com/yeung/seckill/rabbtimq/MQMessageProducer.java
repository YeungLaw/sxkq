package com.yeung.seckill.rabbtimq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author Yeung Law~
 *
 * MQMessageProducer 秒杀消息的生产者
 */

@Service
@Slf4j
public class MQMessageProducer {

    @Resource
    private RabbitTemplate rabbitTemplate;

    // 发送秒杀发送
    public void sendSeckillMessage(String msg) {
        log.info("发送消息-->" + msg);
        rabbitTemplate.convertAndSend("seckillExchange","seckill.message",msg);
    }
}
