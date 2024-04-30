package com.yeung.seckill.rabbtimq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;


/**
 * @author Yeung Law~
 */
@Service
@Slf4j
public class MQReceiver {

    // @Resource
    // private RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = "queue")
    public void receive(Object msg) {
        log.info("接收到消息：" + msg);
    }

    /**
     *
     * 监听队列 queue_fanout
     * @param msg
     */
    @RabbitListener(queues = "queue_fanout01")
    public void receive1(Object msg) {
        log.info("从队列queue_fanout01接收到消息：" + msg);
    }

    @RabbitListener(queues = "queue_fanout02")
    public void receive2(Object msg) {
        log.info("从队列queue_fanout02接收到消息：" + msg);
    }

    /**
     * 监听队列 queue_direct
     * @param msg
     */
    @RabbitListener(queues = "queue_direct01")
    public void queue_direct01(Object msg) {
        log.info("从队列queue_direct01接收到消息：" + msg);
    }

    @RabbitListener(queues = "queue_direct02")
    public void queue_direct02(Object msg) {
        log.info("从队列queue_direct01接收到消息：" + msg);
    }
}
