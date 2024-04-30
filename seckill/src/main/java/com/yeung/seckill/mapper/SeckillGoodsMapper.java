package com.yeung.seckill.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yeung.seckill.pojo.SeckillGoods;

import java.util.List;

/**
 * @author Yeung Law~
 */
public interface SeckillGoodsMapper extends BaseMapper<SeckillGoods> {

    public List<SeckillGoods> findSeckillGoods();
}
