package com.xxm.sublibrary.utils;

import android.content.Context;
import android.util.Log;

/**
 * Created by xlc on 2017/5/24.
 */

public class Ulog {


    private final static boolean EN_DEBUG = true;

    private  static boolean CH_DEBUG;

    public final static String TAG = "love";

    public static void initDebug(Context context)
    {
        CH_DEBUG= Ub.getlog_Debug(context);
    }

    public static void show(String value) {
        if (EN_DEBUG)
            Log.i(TAG, " " + value);
    }
    public static void w(String value)
    {
        if (CH_DEBUG)
            Log.i("Welog", ""+value);
    }
}
