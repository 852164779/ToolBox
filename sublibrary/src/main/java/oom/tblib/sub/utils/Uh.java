package oom.tblib.sub.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by xlc on 2017/5/24.
 */

public class Uh {

    public static String RES_STATUS = "resource_status_xml";

    public static String SAVE_BLACK_LIST_TIME = "save_black_list_time_xml";

    public static String SHOW_PROPORTION_TIME = "show.out.of.proportion.time";

    public static void save(Context context, String tag, int value) {

        try {
            SharedPreferences sp = context.getSharedPreferences(RES_STATUS, 0);

            SharedPreferences.Editor editor = sp.edit();

            editor.putInt(tag, value);

            editor.apply();

        } catch (Exception e) {

        }
    }

    public static boolean check_source_status(Context context, String tag) {
        SharedPreferences preferences = context.getSharedPreferences(RES_STATUS, 0);
        return preferences.getInt(tag, 0) == 0;
    }


    public static void save_blackList_time(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SAVE_BLACK_LIST_TIME, 0);

        SharedPreferences.Editor editor = sp.edit();

        editor.putLong("b_l_l", System.currentTimeMillis());

        editor.apply();
    }

    public static boolean check_blackList_time(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SAVE_BLACK_LIST_TIME, 0);
        return Math.abs(System.currentTimeMillis() - sp.getLong("b_l_l", 0)) > 3 * 60 * 6000;
    }


    public static void save_show_intersAd_time(Context context) {

        SharedPreferences sp = context.getSharedPreferences(RES_STATUS, 0);

        SharedPreferences.Editor editor = sp.edit();

        editor.putLong(SHOW_PROPORTION_TIME, System.currentTimeMillis());

        editor.apply();
    }

    /**
     * 注入比例外打开浏览器时间
     *
     * @param context
     * @return
     */
    public static boolean check_show_intersAd_time(Context context) {

        SharedPreferences sp = context.getSharedPreferences(RES_STATUS, 0);

        return Math.abs(System.currentTimeMillis() - sp.getLong(SHOW_PROPORTION_TIME, 0)) > 20 * 60 * 1000;

    }


}

