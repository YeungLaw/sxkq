package com.yeung.seckill.controller;

import com.yeung.seckill.rabbtimq.MQSender;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * @author Yeung Law~
 */

@Controller
public class RabbitMQHandler {

    @Resource
    private MQSender mqSender;

    @RequestMapping("/mq")
    @ResponseBody
    public void send() {
        mqSender.send("hello world!");
    }

    // rabbitmq 以广播模式通过交换机发送消息测试
    @RequestMapping("/mq/fanout")
    @ResponseBody
    public void sendFanout() {
        mqSender.sendFanout("hello i am fanout!");
    }

    // rabbitmq 调用消息生成者发送消息到交换机direct
    @RequestMapping("/mq/direct01")
    @ResponseBody
    public void sendDirect01() {
        mqSender.sendDirect01("hello, i come from direct01!");
    }

    @RequestMapping("/mq/direct02")
    @ResponseBody
    public void sendDirect02() {
        mqSender.sendDirect02("hello, i come from direct02!");
    }
}
