package com.yeung.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yeung.seckill.pojo.Goods;
import com.yeung.seckill.vo.GoodsVo;

import java.util.List;

/**
 * @author Yeung Law~
 */
public interface GoodsService extends IService<Goods> {

    // 秒杀商品列表
    List<GoodsVo> findGoodsVo();

    GoodsVo findGoodsVoByGoodsId(Long goodsId);
}
