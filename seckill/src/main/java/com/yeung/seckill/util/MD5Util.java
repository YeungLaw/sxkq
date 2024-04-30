package com.yeung.seckill.util;


import org.apache.commons.codec.digest.DigestUtils;

/**
 * @author Yeung Law~
 *
 * MD5工具类，根据密码方案提供相应的加密方法
 */
public class MD5Util {

    //用于加密的salt
    private static final String SLAT = "sdsKS5Qs";

    public static String md5(String src) {
        return DigestUtils.md5Hex(src);
    }

    // 第一次加密加盐
    public static String inputPassToMidPass(String inputPass) {
        String str = SLAT.charAt(0) + inputPass + SLAT.charAt(4);
        return md5(str);
    }

    // 第二次加密加盐
    // 类似 md5( md5(password + salt) + salt)
    public static String midPassToDBPass(String midPass, String salt) {
        String str = salt.charAt(2) + midPass + salt.charAt(salt.length()-1);
        return md5(str);
    }

    // 一次性直接完成两次加密加盐
    // 类似 md5( md5(password + salt) + salt)
    public static String inputPassToDBPass(String inputPass, String salt) {
        String midPass = inputPassToMidPass(inputPass);
        return midPassToDBPass(midPass, salt);
    }

}
