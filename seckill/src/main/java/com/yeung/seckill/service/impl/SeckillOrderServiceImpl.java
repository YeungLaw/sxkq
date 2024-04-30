package com.yeung.seckill.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yeung.seckill.mapper.SeckillOrderMapper;
import com.yeung.seckill.pojo.SeckillOrder;
import com.yeung.seckill.service.SeckillOrderService;
import org.springframework.stereotype.Service;

/**
 * @author Yeung Law~
 */

@Service
public class SeckillOrderServiceImpl
        extends ServiceImpl<SeckillOrderMapper, SeckillOrder>
        implements SeckillOrderService {
}
