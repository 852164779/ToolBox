package mklw.aot.zxjn.u;

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
public class EncodeUtils {

    //    秘钥必须是16位
    //    public static final String keyBytes = "abcdefgabcdefg12";
    public static final String keyBytes = "qsedfgzogn56sd16";

    //AES
    private static final String ALGORITHM = new String(new byte[]{65, 69, 83});
    //AES/ECB/PKCS5Padding
    private static final String TRANSFORMATION = new String(new byte[]{65, 69, 83, 47, 69, 67, 66, 47, 80, 75, 67, 83, 53, 80, 97, 100, 100, 105, 110, 103});

    public static String enCrypt(String input) {
        return enCrypt(input, keyBytes);
    }

    public static String enCrypt(String input, String key) {
        byte[] crypted;
        byte[] return_value = null;
        try {
            SecretKeySpec skey = new SecretKeySpec(key.getBytes(), ALGORITHM);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, skey);
            crypted = cipher.doFinal(input.getBytes());
            return_value = Base64.encode(crypted, Base64.NO_WRAP);

            if (return_value != null) {
                return new String(return_value);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String deCrypt(String input) {
        return deCrypt(input, keyBytes);
    }

    public static String deCrypt(String input, String key) {
        byte[] output = null;
        try {
            SecretKeySpec skey = new SecretKeySpec(key.getBytes(), ALGORITHM);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, skey);
            output = cipher.doFinal(Base64.decode(input, Base64.NO_WRAP));

            if (output != null) {
                return new String(output);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
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