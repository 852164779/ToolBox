package oom.sub.com.jni;

/**
 * Created by xlc on 2017/5/27.
 */

public class Ja {

    static
    {
        System.loadLibrary("signUtil");
    }

    public static native String getPublicKey(Object obj);

}
