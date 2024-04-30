package com.yeung.seckill.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Yeung Law~
 *
 * 该对象用于 测试学习
 * 秒杀消息对象,用于消息队列
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SeckillMessage {

    private User user;
    private Long goodsId;

}
