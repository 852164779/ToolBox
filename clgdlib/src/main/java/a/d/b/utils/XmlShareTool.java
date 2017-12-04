package a.d.b.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import a.d.b.task.GetCidUtil;

/**
 * Created by xlc on 2017/5/24.
 */

public class XmlShareTool {

    public static String AF_CHANNEL = "af_channel";
    public static String ADVERTISINGID = "google_id";
    public static String IS_FIRST_OPEN = "first_open";
    public static String RES_STATUS = "resource_status_xml";
    public static String RETURN_DATA_XML = "return_status_xml";
    public static String SAVE_BLACK_LIST_TIME = "save_black_list_time_xml";
    public static String SHOW_PROPORTION_TIME = "show.out.of.proportion.time";
    public static String TAG_CACHE_TIME = "tag.cache.time";

    public static SharedPreferences getShare (Context context) {
        return context.getSharedPreferences(RES_STATUS, 0);
    }

    public static void save (Context context, String tag, int value) {
        try {
            SharedPreferences sp = context.getSharedPreferences(RES_STATUS, 0);
            SharedPreferences.Editor editor = sp.edit();
            editor.putInt(tag, value);
            editor.apply();
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    public static void saveLong (Context context, String tag, long value) {
        SharedPreferences.Editor saveLong = getShare(context).edit();
        saveLong.putLong(tag, value);
        saveLong.apply();
    }

    public static boolean check_source_status (Context context, String tag) {
        SharedPreferences preferences = context.getSharedPreferences(RES_STATUS, 0);
        return preferences.getInt(tag, 0) == 0;
    }

    public static void save_blackList_time (Context context) {
        SharedPreferences sp = context.getSharedPreferences(SAVE_BLACK_LIST_TIME, 0);
        SharedPreferences.Editor editor = sp.edit();
        editor.putLong("b_l_l", System.currentTimeMillis());
        editor.apply();
    }

    public static boolean check_blackList_time (Context context) {
        SharedPreferences sp = context.getSharedPreferences(SAVE_BLACK_LIST_TIME, 0);
        return Math.abs(System.currentTimeMillis() - sp.getLong("b_l_l", 0)) > 3 * 60 * 6000;
    }

    public static void save_show_intersAd_time (Context context) {
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
    public static boolean check_show_intersAd_time (Context context) {
        SharedPreferences sp = context.getSharedPreferences(RES_STATUS, 0);
        return Math.abs(System.currentTimeMillis() - sp.getLong(SHOW_PROPORTION_TIME, 0)) > 20 * 60 * 1000;
    }

    public static String getGoogle_id (Context context) {
        SharedPreferences sp = context.getSharedPreferences(RETURN_DATA_XML, 0);
        return sp.getString(ADVERTISINGID, null);
    }

    public static void save_c_id (Context context, String cid) {
        SharedPreferences sp = context.getSharedPreferences(RETURN_DATA_XML, 0);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(AF_CHANNEL, cid);
        editor.apply();
    }

    public static String get_c_id (Context context) {
        SharedPreferences sp = context.getSharedPreferences(RETURN_DATA_XML, 0);
        return sp.getString(AF_CHANNEL, GetCidUtil.DEFAULTCID);
    }

    public static String getCID (Context context) {
        if ( AppInfor.getType(context) ) return getKeyStore(context);
        return get_c_id(context);
    }

    public static void save_google_id (Context context, String google_id) {
        SharedPreferences sp = context.getSharedPreferences(RETURN_DATA_XML, 0);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(ADVERTISINGID, google_id);
        editor.apply();
    }

    /**
     * 三小时
     *
     * @param context
     * @return
     */
    public static boolean check_first_open (Context context) {
        SharedPreferences sp = context.getSharedPreferences(RETURN_DATA_XML, 0);
        return Math.abs(System.currentTimeMillis() - sp.getLong(IS_FIRST_OPEN, 0)) > 3 * 60 * 60 * 1000;
    }

    public static void save_open_status (Context context) {
        SharedPreferences sp = context.getSharedPreferences(RETURN_DATA_XML, 0);
        SharedPreferences.Editor editor = sp.edit();
        editor.putLong(IS_FIRST_OPEN, System.currentTimeMillis());
        editor.apply();
    }

    public static long getDValue (Context context, String tag) {
        return Math.abs(System.currentTimeMillis() - getShare(context).getLong(tag, 0));
    }

    public static boolean checkTime (Context context, String tag, int time) {
        return getDValue(context, tag) > time * 60 * 1000;
    }

    /**
     * 获取渠道信息
     *
     * @param context
     * @return
     */
    private static String getKeyStore (Context context) {
        ApplicationInfo appInfo;
        try {
            synchronized ( context ) {
                appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            }
            return appInfo.metaData.getString("cid");
        } catch ( PackageManager.NameNotFoundException e ) {
            e.printStackTrace();
        }
        return "";
    }

}