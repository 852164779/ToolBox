package com.newfresh.easyclean.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import static com.newfresh.easyclean.notification.NotifictionUtil.ACTION_RELEASE_FLASH;

/**
 * Created by hwl on 2017/10/13.
 */

public class OtherUtil {

    private static final Set<String> RTL;

    static {
        Set<String> lang = new HashSet<String>();
        lang.add("ar");
        lang.add("dv");
        lang.add("fa");
        lang.add("ha");
        lang.add("he");
        lang.add("iw");
        lang.add("ji");
        lang.add("ps");
        lang.add("ur");
        lang.add("yi");
        RTL = Collections.unmodifiableSet(lang);
    }

    /**
     * 判断当前手机语言是否是RTL
     */
    public static boolean isTextRTL (Locale locale) {
        return RTL.contains(locale.getLanguage());
    }


    public static void hideNaviga (Activity context) {
        context.getWindow().getDecorView().setSystemUiVisibility(//
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }

    public static void releas_flash_ (Context context) {
        Intent intent = new Intent();
        intent.setAction(ACTION_RELEASE_FLASH + context.getPackageName());
        context.sendBroadcast(intent);
    }

    public static int getSdkVersion () {
        return android.os.Build.VERSION.SDK_INT;
    }

    public static Map<String, String> getWebHead () {
        Map<String, String> header = new HashMap<>();
        header.put("X-Requested-With", "com.android.chrome");//默认是应用包名
        return header;
    }
}
