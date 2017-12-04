package oom.tblib.sub.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by xlc on 2017/5/24.
 */

public class Umd5 {

    public String str;

    public static String encrypt(String paramString) {
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
}
