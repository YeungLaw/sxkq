package com.yeung.seckill.exception;

import com.yeung.seckill.vo.RespBean;
import com.yeung.seckill.vo.RespBeanEnum;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Yeung Law~
 *
 * 全局异常处理器
 * 当Controller 处理发生异常时，被全局异常处理器捕获
 * 全局异常处理器会将结果返回给用户
 *
 * 因此返回为json格式时必须使用 @ResponseBody 注解，否则会报错！！！
 */
@ControllerAdvice
@ResponseBody
// @RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public RespBean ExceptionHandle(Exception e) {
        if (e instanceof GlobalException) {
            GlobalException ex = (GlobalException) e;
            return RespBean.error(ex.getRespBeanEnum());
        }else if ( e instanceof BindException) {

            BindException ex = (BindException) e;
            RespBean respBean = RespBean.error(RespBeanEnum.BING_ERROR);
            respBean.setMessage("参数校验异常~：" +
                    ex.getBindingResult().getAllErrors().get(0).getDefaultMessage());
            return respBean;
        }
        // return RespBean.error(RespBeanEnum.ERROR);
        throw new RuntimeException(e);
    }
}
