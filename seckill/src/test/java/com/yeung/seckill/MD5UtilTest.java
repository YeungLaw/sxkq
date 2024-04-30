package com.yeung.seckill;

import com.yeung.seckill.util.MD5Util;
import org.junit.jupiter.api.Test;

/**
 * @author Yeung Law~
 */
public class MD5UtilTest {


    @Test
    public void md5() {
        String pass = MD5Util.md5("12345");
        String toDBPass = MD5Util.midPassToDBPass("37a2e25304f1c13529faa501fb48cc49", "5sdfJDSo0d");

        System.out.println(toDBPass);
    }
}
