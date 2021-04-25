package org.lyfy.beyond.encrypt.plugin.util;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class AESUtil {

    private static final String IV_STRING = "Dianshang@123456";
    private static final String ENCODING = "UTF-8";

    private static final String DB_FIELD_ENCRYPT_KEY = "db.encrypt.key";

    public static String getDBFieldEncrtptKey() {
        return System.getProperty(DB_FIELD_ENCRYPT_KEY, IV_STRING);
    }

    public static String encryptAES(String content, String key)
            throws InvalidKeyException, NoSuchAlgorithmException,
            NoSuchPaddingException, UnsupportedEncodingException,
            InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        byte[] byteContent = content.getBytes(ENCODING);
        byte[] enCodeFormat = key.getBytes(ENCODING);
        SecretKeySpec secretKeySpec = new SecretKeySpec(enCodeFormat, "AES");
        byte[] initParam = IV_STRING.getBytes(ENCODING);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(initParam);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
        byte[] encryptedBytes = cipher.doFinal(byteContent);
        String base64 = Base64.getEncoder().encodeToString(encryptedBytes);
        return URLEncoder.encode(base64, ENCODING);
    }


    public static String decryptAES(String content, String key)
            throws InvalidKeyException, NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidAlgorithmParameterException,
            IllegalBlockSizeException, BadPaddingException, IOException {
        content = URLDecoder.decode(content, ENCODING);
        byte[] encryptedBytes = Base64.getDecoder().decode(content);
        byte[] enCodeFormat = key.getBytes(ENCODING);
        SecretKeySpec secretKey = new SecretKeySpec(enCodeFormat, "AES");
        byte[] initParam = IV_STRING.getBytes(ENCODING);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(initParam);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);
        byte[] result = cipher.doFinal(encryptedBytes);
        return new String(result, ENCODING);
    }

    public static void main(String[] args) throws Exception {
        String content = "18601721519";
        System.out.println("加密前：" + content);
        String key = IV_STRING;
        System.out.println("加密密钥和解密密钥：" + key);
        String encrypt = encryptAES(content, key);
        System.out.println("加密后：" + encrypt);
        String decrypt = decryptAES(encrypt, key);
        System.out.println("解密后：" + decrypt);
    }
}
