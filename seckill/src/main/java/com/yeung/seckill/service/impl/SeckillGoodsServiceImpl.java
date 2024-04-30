package com.yeung.seckill.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yeung.seckill.mapper.SeckillGoodsMapper;
import com.yeung.seckill.pojo.SeckillGoods;
import com.yeung.seckill.service.SeckillGoodsService;
import org.springframework.stereotype.Service;

/**
 * @author Yeung Law~
 */
@Service
public class SeckillGoodsServiceImpl
        extends ServiceImpl<SeckillGoodsMapper, SeckillGoods>
        implements SeckillGoodsService {
}
