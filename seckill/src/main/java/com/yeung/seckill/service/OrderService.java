package com.yeung.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yeung.seckill.pojo.Order;
import com.yeung.seckill.pojo.User;
import com.yeung.seckill.vo.GoodsVo;

/**
 * @author Yeung Law~
 */
public interface OrderService extends IService<Order> {
    // 描述是
    Order seckill(User user, GoodsVo goodsVo);

    // 生成秒杀路径（唯一值）
    String createPath(User user, Long goodsId);

    // 秒杀路径校验
    boolean checkPath(User user, Long goodsId, String path);

    // 验证用户输入的验证码
    boolean checkCaptcha(User user, Long goosId, String captcha);
}
