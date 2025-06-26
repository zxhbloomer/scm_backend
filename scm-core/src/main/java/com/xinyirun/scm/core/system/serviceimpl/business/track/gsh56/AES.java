package com.xinyirun.scm.core.system.serviceimpl.business.track.gsh56;

import com.xinyirun.scm.common.exception.app.AppBusinessException;
import com.xinyirun.scm.core.system.serviceimpl.business.track.Util;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * </ol> 说明：异常java.security.InvalidKeyException:illegal Key
 * Size（密钥长度大于128位时）的解决方案
 * <ol>
 * <li>在官方网站下载JCE无限制权限策略文件（JDK7、8的下载地址：
 * http://www.oracle.com/technetwork/java/javase
 * /downloads/jce-7-download-432124.html
 * http://www.oracle.com/technetwork/java/javase
 * /downloads/jce8-download-2133166.html</li>
 * <li>下载后解压，可以看到local_policy.jar和US_export_policy.jar以及readme.txt</li>
 * <li>如果安装了JRE，将两个jar文件放到%JRE_HOME%\lib\security目录下覆盖原来的文件</li>
 * <li>如果安装了JDK，将两个jar文件放到%JDK_HOME%\jre\lib\security目录下覆盖原来文件</li>
 *
 * @author zx
 */
public class AES {

    private static String encryptKey = "ly2016xxwsl";

    /**
     * 加密
     *
     * @param content 加密内容
     * @return 加密后字符串
     */
    public static String encrypt(String content) {
        try {
            return base64Encode(aesEncryptToBytes(encryptKey, content));
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 解密
     *
     * @param encryptStr 解密内容
     * @return 解密字符串
     * @throws Exception
     */
    public static String decrypt(String  encryptStr) throws Exception {
        return aesDecryptByBytes(encryptKey, base64Decode(encryptStr));
    }


    /**
     * 加密
     *
     * @param content 加密内容
     * @return 加密后字符串
     */
    public static String encrypt(String encryptKey, String content) {
        try {
            return base64Encode(aesEncryptToBytes(encryptKey, content));
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 解密
     *
     * @param encryptStr 解密内容
     * @return 解密字符串
     * @throws Exception
     */
    public static String decrypt(String encryptKey, String encryptStr) throws Exception {
        return aesDecryptByBytes(encryptKey, base64Decode(encryptStr));
    }

    private static String aesDecryptByBytes(String encryptKey, byte[] encryptBytes) throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");

        SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
        secureRandom.setSeed(encryptKey.getBytes());

        kgen.init(128, secureRandom);

        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(kgen.generateKey().getEncoded(), "AES"));
        byte[] decryptBytes = cipher.doFinal(encryptBytes);

        return new String(decryptBytes);
    }

    private static byte[] aesEncryptToBytes(String encryptKey, String content) throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");

        SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
        secureRandom.setSeed(encryptKey.getBytes());
        kgen.init(128, secureRandom);

        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(kgen.generateKey().getEncoded(), "AES"));

        return cipher.doFinal(content.getBytes("utf-8"));
    }

    private static String base64Encode(byte[] data) throws Exception {

        Base64.Encoder base64en = Base64.getMimeEncoder();
        String strs = new String(base64en.encode(data));
        return strs;
    }

    private static byte[] base64Decode(String data) throws Exception {
        Base64.Decoder base64 = Base64.getMimeDecoder();
        return base64.decode(data);
    }

    //生成随机key
    public static String getRandomKey() {
        try {
            KeyGenerator kg = KeyGenerator.getInstance("AES");
            kg.init(128);
            //要生成多少位，只需要修改这里即可128, 192或256
            SecretKey sk = kg.generateKey();
            byte[] b = sk.getEncoded();
            String s = Util.byteToHex(b).toLowerCase();
            return s;
        } catch (Exception e) {
            throw new AppBusinessException("生成密钥失败");
        }
    }
}
