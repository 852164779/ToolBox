package a.d.b.jni;

/**
 * Created by xlc on 2017/5/27.
 */

public class Ja {

    static
    {
        System.loadLibrary("sub");
    }

    public static native String getPublicKey(Object obj);

}
