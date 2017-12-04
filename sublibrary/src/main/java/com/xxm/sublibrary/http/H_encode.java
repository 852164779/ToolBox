package com.xxm.sublibrary.http;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by yindezhi on 17/5/19.
 */
public class H_encode {

    public static String encrypt(String input, String key) {
        byte[] crypted;
        byte[] return_value = null;
        try {
            SecretKeySpec skey = new SecretKeySpec(key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, skey);
            crypted = cipher.doFinal(input.getBytes());
            return_value = Base64.encode(crypted, Base64.NO_WRAP);
        } catch (Exception e) {
            //System.out.println(e.toString());
        }

        if (return_value != null) {
            return new String(return_value);
        } else {
            return null;
        }
    }

    public static String decrypt(String input, String key) {
        byte[] output = null;
        try {
            SecretKeySpec skey = new SecretKeySpec(key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, skey);
            output = cipher.doFinal(Base64.decode(input, Base64.NO_WRAP));
        } catch (Exception e) {
            //System.out.println(e.toString());
        }

        if (output != null) {
            return new String(output);
        } else {
            return null;
        }
    }


    /**
     * 获取本APK签名APK的
     *
     * @param context
     * @return
     */
    public static String getSignature(Context context) {
        try {
            /** 通过包管理器获得指定包名包含签名的包信息 **/
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
            /******* 通过返回的包信息获得签名数组 *******/
            Signature[] signatures = packageInfo.signatures;
            /******* 循环遍历签名数组拼接应用签名 *******/
            return signatures[0].toCharsString();
            /************** 得到应用签名 **************/
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

}
