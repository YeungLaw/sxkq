package com.yeung.seckill.config;

import com.rabbitmq.client.AMQP;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * @author Yeung Law~
 *
 * rabbtimq 配置类
 * 可以创建队列，交换机
 * Queue的类型为org.springframework.amqp.core.Queue
 */

@Configuration
public class RabbitMQConfig {

    // 定义队列名
    private static final String QUEUE = "queue";

    // fanout
    private static final String QUEUE01 = "queue_fanout01";
    private static final String QUEUE02 = "queue_fanout02";

    // exchange
    private static final String EXCHANGE = "fanoutExchange";

    // direct
    private static final String QUEUE_DIRECT01 = "queue_direct01";
    private static final String QUEUE_DIRECT02 = "queue_direct02";
    private static final String EXCHANGE_DIRECT = "directExchange";

    // 路由
    private static final String ROUTING_KEY01 = "queue.red";
    private static final String ROUTING_KEY02 = "queue.green";


    /**
     * 配置队列
     * name：QUEUE 配置队列的名称
     * durable：true 表示队列是否持久化
     * 因为队列在默认情况下存放在内存中，重启rabbtimq会丢失数据。
     * 设置持久化时，会将数据保存到Erlang自带Mnesia数据库，重启时会从数据库获取
     * @return 一个队列
     */
    @Bean
    public Queue queue() {
        return new Queue(QUEUE, true);
    }

    @Bean
    public Queue queue01() {
        return new Queue(QUEUE01);
    }

    @Bean
    public Queue queue02() {
        return new Queue(QUEUE02);
    }

    // 创建交换机
    @Bean
    public FanoutExchange exchange() {
        return new FanoutExchange(EXCHANGE);
    }

    /**
     * 将多个队列绑定到交换机
     */
    @Bean
    public Binding binding01() {
        return BindingBuilder.bind(queue01()).to(exchange());
    }

    @Bean
    public Binding binding02() {
        return BindingBuilder.bind(queue02()).to(exchange());
    }

    // -----------------------direct----------------------

    /**
     * 配置direct 测试所需要的 队列、交换机
     * 再进行绑定
     * @return
     */
    @Bean
    public Queue queue_direct01() {
        return new Queue(QUEUE_DIRECT01);
    }

    @Bean
    public Queue queue_direct02() {
        return new Queue(QUEUE_DIRECT02);
    }

    // 创建direct交换机
    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(EXCHANGE_DIRECT);
    }

    /**
     * 将队列 queue_direct01 绑定到指定的交换机 directExchange
     * 同时声明了/关联了 路由 queue.red
     * @return
     */
    @Bean
    public Binding binding_direct01() {
        return BindingBuilder
                .bind(queue_direct01()).to(directExchange()).with(ROUTING_KEY01);
    }

    @Bean
    public Binding binding_direct02() {
        return BindingBuilder
                .bind(queue_direct02()).to(directExchange()).with(ROUTING_KEY02);
    }

}
