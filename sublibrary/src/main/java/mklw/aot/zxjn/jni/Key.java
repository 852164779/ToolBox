package mklw.aot.zxjn.jni;


public class Key {

    static {
        System.loadLibrary("signUtil");
    }

    public static native String getPublicKey(Object obj);
}