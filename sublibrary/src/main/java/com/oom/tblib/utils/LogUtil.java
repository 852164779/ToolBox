package com.oom.tblib.utils;

import android.util.Log;

/**
 * Created by xlc on 2017/5/24.
 */

public class LogUtil {

    public final static String TAG = "love";

    public static boolean States = true;

    public static void show(String value) {
        Log.i(TAG, " " + value);
    }

    public static void showe(String value) {
        Log.e(TAG, " " + value);
    }

    public static void w(String value) {
        Log.i("Welog", "" + value);
    }

    public static void initSuccess() {
        if (States) {
            States = false;
            Log.i("SDK", "init Success");
        }
    }

    public static void initFailed() {
        Log.i("SDK", "init failed (no cid) ");
    }
}
