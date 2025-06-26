package com.xinyirun.scm.common.utils;

import com.google.common.collect.Maps;

import java.util.Map;
import java.util.Random;

/**
 * @author zxh
 * @date 2019年 07月22日 22:14:13
 */
public class CodeGenerator {
    public static final String ALLCHAR = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String ALL_ALPHABET_UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private static Map<String, Integer> prefixs = Maps.newHashMap();
    private static char[] chars = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
            'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};


    /**
     * @param num 返回随机数的位数, 如3则可能返回029
     * @return
     */
    public static String randomInt(int num) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < num; i++) {
            sb.append((int) (Math.random() * (10)));
        }
        return sb.toString();
    }

    /**
     * 生成指定范围内的随机数
     * @param min
     * @param max
     * @return
     */
    public static int randomInt(int min, int max) {
        Random rand = new Random();
        int randomNum = rand.nextInt((max - min) + 1) + min;
        return randomNum;
    }

    /**
     * 字符串不足位数补长
     *
     * @param num   长度
     * @param value 值
     * @return
     */
    public static String addLeftZeroForNum(int num, int value) {
        return String.format("%" + num + "d", value).replace(" ", "0");
    }
    /**
     * 字符串不足位数补长
     *
     * @param num   长度
     * @param value 值
     * @return
     */
    public static String addLeftZeroForNum(int num, Long value) {
        return String.format("%" + num + "d", value).replace(" ", "0");
    }

    /**
     * 字符串不足位数补长
     *
     * @param num   长度
     * @param value 值
     * @return
     */
    public static String addRightZeroForNum(int num, int value) {
        return String.format("%-" + num + "d", value).replace(" ", "0");
    }

    /**
     * 返回一个定长的随机字符串(只包含大小写字母、数字)
     *
     * @param length 随机字符串长度
     * @return 随机字符串
     */
    public static String randomChar(int length) {
        StringBuffer sb = new StringBuffer();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(ALLCHAR.charAt(random.nextInt(ALLCHAR.length())));
        }
        return sb.toString().toUpperCase();
    }

    /**
     * 返回一个定长的随机字符串(只包含大写字母)
     *
     * @param length 随机字符串长度
     * @return 随机字符串
     */
    public static String randomAlphabet(int length) {
        StringBuffer sb = new StringBuffer();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(ALL_ALPHABET_UPPERCASE.charAt(random.nextInt(ALL_ALPHABET_UPPERCASE.length())));
        }
        return sb.toString().toUpperCase();
    }



    /**
     * The byte[] returned by MessageDigest does not have a nice textual
     * representation, so some form of encoding is usually performed.
     * <p>
     * This implementation follows the example of David Flanagan's book
     * "Java In A Nutshell", and converts a byte array into a String of hex
     * characters.
     * <p>
     * Another popular alternative is to use a "Base64" encoding.
     */
    private static String hexEncode(byte[] aInput) {
        StringBuilder result = new StringBuilder();
        for (int idx = 0; idx < aInput.length; ++idx) {
            byte b = aInput[idx];
            result.append(chars[(b & 0xf0) >> 4]);
            result.append(chars[b & 0x0f]);
        }
        return result.toString();
    }

}