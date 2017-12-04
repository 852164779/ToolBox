package com.newfresh.easyclean.util;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;

/**
 * Created by hwl on 2017/10/13.
 */

public class XmlShareUtil {

    private static final String XML_SHARE_NAME = "android.easyclean.share.name";

    private static final String TAG_BATTERY_SLEEP = "tag.easyclean.sleep.time";


    public static SharedPreferences getShare (Context context) {
        return context.getSharedPreferences(XML_SHARE_NAME, 0);
    }

    public static void save_sleep_time (Context context, int vules) {
        SharedPreferences.Editor editor = getShare(context).edit();
        editor.putInt(TAG_BATTERY_SLEEP, vules);
        editor.apply();
    }

    public static int get_sleep_time (Context context) {
        return getShare(context).getInt(TAG_BATTERY_SLEEP, 60000);
    }

    public static void saveSharedInfor (Context context, Map<String, Long> data) {
        SharedPreferences.Editor editor = getShare(context).edit();
        for ( Map.Entry<String, Long> entry : data.entrySet() ) {
            editor.putLong(entry.getKey(), entry.getValue());
        }
        editor.commit();
    }

    public static long getSharePreferenceLong (Context context, String strName) {
        return getShare(context).getLong(strName, 0);
    }

}
