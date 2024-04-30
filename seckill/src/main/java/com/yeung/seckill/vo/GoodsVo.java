package com.yeung.seckill.vo;

import com.yeung.seckill.pojo.Goods;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Yeung Law~
 *
 * GoodsVo: 对应就是显示再秒杀商品列表的信息
 *
 * 其实就是存放两表联合查询的数据
 * 通过继承关系，可以减少属性值的代码编写
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoodsVo extends Goods {

    private BigDecimal seckillPrice;

    private Integer stockCount;

    private Date startDate;

    private Date endDate;

    //如果后面有需求，也可以做修改
}
