package com.feng.util;

import cn.hutool.crypto.SmUtil;
import com.feng.exception.AppException;


public class PasswordUtils {
    /**
     * 盐,给密码加盐，进行哈希散列加密，加密后的密码不可逆
     */
    private static final String SALT = "%$Pymjl*&123^CUIT.epoch.pymjl*&^%$$";

    /**
     * 加密
     *
     * @param password 密码
     * @return {@code String}
     */
    public static String encrypt(String password) {
        return encrypt(password, SALT);
    }

    /**
     * 匹配
     *
     * @param password          密码
     * @param encryptedPassword 加密密码
     * @return {@code Boolean}
     */
    public static Boolean match(String password, String encryptedPassword) {
        return match(password, encryptedPassword, SALT);
    }

    /**
     * 匹配
     *
     * @param password          密码
     * @param encryptedPassword 加密密码
     * @param salt              盐
     * @return {@code Boolean}
     */
    private static Boolean match(String password, String encryptedPassword, String salt) {
        //密码不能为空
        if (null == password || "".equals(password)) {
            throw new AppException("密码为空");
        }
        return encryptedPassword.equals(encrypt(password, salt));
    }

    /**
     * 加密
     *
     * @param password 密码
     * @param salt     盐
     * @return {@code String}
     */
    private static String encrypt(String password, String salt) {
        return SmUtil.sm3(salt + password);
    }
}
