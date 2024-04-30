package com.yeung.seckill.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yeung.seckill.pojo.Goods;
import com.yeung.seckill.vo.GoodsVo;

import java.util.List;

/**
 * @author Yeung Law~
 */
public interface GoodsMapper extends BaseMapper<Goods> {

    // 获取商品列表
    List<GoodsVo> findGoodsVo();

    GoodsVo findGoodsVoByGoodsId(Long goodsId);
}
