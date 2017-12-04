package com.oom.tblib.utils;

import android.util.Base64;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by xlc on 2017/5/24.
 */

public class EncodeUtil {

    //    秘钥必须是16位
    //    public static final String keyBytes = "abcdefgabcdefg12";
    public static final String keyBytes = "ZdiJloNq12dfA59q";

    public static String encryptByMD5(String paramString) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(paramString.getBytes());
            byte[] m = md5.digest();
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < m.length; i++) {
                sb.append(m[i]);
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String encryptByAES(String input) {
        return encryptByAES(input, keyBytes);
    }

    public static String encryptByAES(String input, String key) {
        byte[] crypted;
        byte[] return_value = null;
        try {
            SecretKeySpec skey = new SecretKeySpec(key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, skey);
            crypted = cipher.doFinal(input.getBytes());
            return_value = Base64.encode(crypted, Base64.NO_WRAP);
        } catch (Exception e) {
            System.out.println(e.toString());
        }

        if (return_value != null) {
            return new String(return_value);
        } else {
            return null;
        }
    }

    public static String decryptByAES(String inputy) {
        return decryptByAES(inputy, keyBytes);
    }

    public static String decryptByAES(String input, String key) {
        byte[] output = null;
        try {
            SecretKeySpec skey = new SecretKeySpec(key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, skey);
            output = cipher.doFinal(Base64.decode(input, Base64.NO_WRAP));
        } catch (Exception e) {

        }

        if (output != null) {
            return new String(output);
        } else {
            return null;
        }
    }

}
