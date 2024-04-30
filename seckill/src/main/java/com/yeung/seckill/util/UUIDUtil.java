package com.yeung.seckill.util;

import org.junit.jupiter.api.Test;

import java.util.UUID;

/**
 * @author Yeung Law~
 *
 * 随机生成字符串
 * 用于生成sessionid
 */
public class UUIDUtil {

    public static String uuid() {
        //默认生成的字符串为xxx-yyy-zzz
        return UUID.randomUUID().toString().replace("-","");
    }

    @Test
    public void test(){
        System.out.println(uuid());
    }

}
