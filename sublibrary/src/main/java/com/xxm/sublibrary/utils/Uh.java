package com.xxm.sublibrary.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by xlc on 2017/5/24.
 */

public class Uh {

    public static String RETURN_DATA_XML = "resource_status_xml";

    public static String SAVE_BLACK_LIST_TIME = "save_black_list_time_xml";

    public static void save(Context context, String tag, int value) {

        try {
            SharedPreferences sp = context.getSharedPreferences(RETURN_DATA_XML, 0);

            SharedPreferences.Editor editor = sp.edit();

            editor.putInt(tag, value);

            editor.apply();

        } catch (Exception e) {

        }
    }

    public static boolean check_source_status(Context context,String tag) {

        SharedPreferences sp = context.getSharedPreferences(RETURN_DATA_XML, 0);

        return sp.getInt(tag, 0) == 0;
    }



    public static void save_blackList_time(Context context)
    {
        SharedPreferences sp = context.getSharedPreferences(SAVE_BLACK_LIST_TIME, 0);

        SharedPreferences.Editor editor = sp.edit();

        editor.putLong("b_l_l", System.currentTimeMillis());

        editor.apply();
    }


    public static boolean check_blackList_time(Context context)
    {
        SharedPreferences sp=context.getSharedPreferences(SAVE_BLACK_LIST_TIME,0);

        return Math.abs(System.currentTimeMillis()-sp.getLong("b_l_l",0))>3*60*6000;
    }
}
