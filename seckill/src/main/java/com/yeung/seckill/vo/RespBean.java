package com.yeung.seckill.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Yeung Law~
 *
 * 用来 返回信息 给 用户类
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RespBean {

    private long code;
    private String message;

    private Object obj;

    //成功后 不携带数据
    public static RespBean success() {
        return success(null);
    }

    //成功后 同时携带数据
    public static RespBean success(Object data) {
        return new RespBean(RespBeanEnum.SUCCESS.getCode(),
                RespBeanEnum.SUCCESS.getMessage(), data);
    }

    //失败后,不携带数据
    public static RespBean error(RespBeanEnum respBeanEnum) {
        return new RespBean(respBeanEnum.getCode(),
                respBeanEnum.getMessage(), null);
    }

    //失败后,携带数据
    public static RespBean error(RespBeanEnum respBeanEnum, Object data) {
        return new RespBean(respBeanEnum.getCode(),
                respBeanEnum.getMessage(), data);
    }
}
