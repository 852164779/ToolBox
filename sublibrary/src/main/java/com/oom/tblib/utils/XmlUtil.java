package com.oom.tblib.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import java.util.Calendar;

/**
 * Created by xlc on 2017/5/24.
 */

public class XmlUtil {

    public static String SHARE_NAME = "resource.status.share";
    public static String SHARE_NAME_DDL = "resource.status.ddl.share";

    /**
     * 黑名单时间
     */
    public static String TAG_BLACK_TIME = "save.black.list.time";

    /**
     * 注入比例之外打开WebView时间
     */
    public static String TAG_PROPORTION_WEB = "show.out.of.proportion.time";

    /**
     * 保存月份
     */
    public static String TAG_MONTH = "check.month.time";

    /**
     * JS下载时间
     */
    public static String TAG_JS_TIME = "save.down.js.time";

    /**
     * 缓存时间
     */
    public static String TAG_CACHE_TIME = "save.down.cache.time";

    /**
     * 联网时间
     */
    public static String TAG_CONNECT_TIME = "save.down.connection.time";

    /**
     * 上一次执行时间
     */
    public static String TAG_EXECUTE_TIME = "save.load.webview.time";

    /**
     * 黑名单状态
     */
    public static String TAG_BLACK_STATE = "save.states.black.time";

    /**
     * 广播触发服务时间
     */
    public static String TAG_RECEIVER_LOAD = "save.load.receiver.time";

    public static SharedPreferences getShare(Context context) {
        return context.getSharedPreferences(SHARE_NAME, 0);
    }

    public static void saveInt(Context context, String tag, int value) {
        try {
            SharedPreferences.Editor editor = getShare(context).edit();
            editor.putInt(tag, value);
            editor.apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveCurrentTime(Context context, String tag) {
        try {
            SharedPreferences.Editor editor = getShare(context).edit();
            editor.putLong(tag, System.currentTimeMillis());
            editor.apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static long checkTime(Context context, String tag) {
        return Math.abs(System.currentTimeMillis() - getShare(context).getLong(tag, 0));
    }

    public static boolean checkSourceStatus(Context context, String tag) {
        return getShare(context).getInt(tag, 0) == 0;
    }

    public static void saveBlackListTime(Context context) {
        saveCurrentTime(context, TAG_BLACK_TIME);
    }

    public static boolean checkBlackListTime(Context context) {
        return checkTime(context, TAG_BLACK_TIME) > 3 * 60 * 6000;
    }

    public static void save_show_intersAd_time(Context context) {
        saveCurrentTime(context, TAG_PROPORTION_WEB);
    }

    public static boolean check_show_intersAd_time(Context context) {
        return checkTime(context, TAG_PROPORTION_WEB) > 20 * 60 * 1000;
    }

    public static boolean checkTimeAboveMonth(Context context) {
        SharedPreferences preferences = getShare(context);
        int lastMonth = preferences.getInt(TAG_MONTH, 0);
        int currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;

        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(TAG_MONTH, currentMonth);
        editor.apply();

        if (currentMonth != lastMonth) {
            return true;
        }

        return false;
    }

    public static void saveDownJsTime(Context context) {
        saveCurrentTime(context, TAG_JS_TIME);
    }

    public static boolean checkDownJsTime(Context context) {
        return checkTime(context, TAG_JS_TIME) > 72 * 60 * 60 * 1000;
    }

    public static void saveCacheTime(Context context) {
        saveCurrentTime(context, TAG_CACHE_TIME);
    }

    /**
     * 检测缓存是否满足条件
     *
     * @return
     */
    public static boolean checkCacheTime(Context context) {
        return checkTime(context, TAG_CACHE_TIME) > 6 * 60 * 60 * 1000;
    }

    /**
     * 保存联网信息
     */
    public static void saveConnectTime(Context context) {
        saveCurrentTime(context, TAG_CONNECT_TIME);
    }

    /**
     * 检测联网是否满足条件
     *
     * @return
     */
    public static boolean checkConnectTime(Context context) {
        return checkTime(context, TAG_CONNECT_TIME) > 6 * 60 * 60 * 1000;
    }

    public static void saveExecuteTime(Context context) {
        saveCurrentTime(context, TAG_EXECUTE_TIME);
    }

    public static boolean checkExecuteTime(Context context) {
        return checkTime(context, TAG_EXECUTE_TIME) > 20 * 60000;
    }

    public static void saveBlackState(Context context, int value) {
        saveInt(context, TAG_BLACK_STATE, value);
    }

    public static boolean checkBlackState(Context context) {
        return getShare(context).getInt(TAG_BLACK_STATE, 0) == -1 || getShare(context).getInt(TAG_BLACK_STATE, 0) == 0;
    }

    public static int getBlackState(Context context) {
        return getShare(context).getInt(TAG_BLACK_STATE, 0);
    }

    public static void saveReceiverLoadTime(Context context) {
        saveCurrentTime(context, TAG_RECEIVER_LOAD);
    }

    public static boolean checkReceiverLoadTime(Context context) {
        return checkTime(context, TAG_RECEIVER_LOAD) > 3000;
    }

    /********************************************DDL 相关**********************************************/
    // TODO DDL 相关
    /**
     * GoogleID
     */
    public static String TAG_GOOGLE_ID = "xxm.google.id.pre";

    /**
     * 通过链接获取到的渠道号
     */
    public static String TAG_CID_CHANNEL = "xxm.channel.cid.pre";

    /**
     * 通过广播获取的渠道号
     */
    public static String TAG_CID_RECEIVER = "xxm.receiver.cid.pre";

    /**
     * 通过广播获取的渠道号
     */
    public static String TAG_IS_FIRST = "xxm.first.open.pre";

    public static SharedPreferences getShareForDDL(Context context) {
        return context.getSharedPreferences(SHARE_NAME_DDL, 0);
    }

    public static void saveStrForDDL(Context context, String tag, String value) {
        SharedPreferences.Editor editor = getShareForDDL(context).edit();
        editor.putString(tag, value);
        editor.apply();
    }

    public static long checkTimeForDDL(Context context, String tag) {
        return Math.abs(System.currentTimeMillis() - getShareForDDL(context).getLong(tag, 0));
    }

    public static void saveGoogleID(Context context, String str) {
        saveStrForDDL(context, TAG_GOOGLE_ID, str);
    }

    public static String getGoogleID(Context context) {
        return getShareForDDL(context).getString(TAG_GOOGLE_ID, null);
    }

    public static void saveChannelCid(Context context, String cid) {
        saveStrForDDL(context, TAG_CID_CHANNEL, cid);
    }

    public static void saveReceiverCid(Context context, String rid) {
        saveStrForDDL(context, TAG_CID_RECEIVER, rid);
    }

    public static String getChannelCID(Context context) {
        return getShareForDDL(context).getString(TAG_CID_CHANNEL, "");
    }

    public static String getReceiverCID(Context context) {
        return getShareForDDL(context).getString(TAG_CID_RECEIVER, "");
    }

    public static String getCID(Context context) {
        if (TextUtils.isEmpty(getReceiverCID(context))) {
            return getChannelCID(context);
        }
        return getReceiverCID(context);
    }

    public static boolean checkOpenTime(Context context) {
        return checkTimeForDDL(context, TAG_IS_FIRST) > 6 * 60 * 60 * 1000;
    }

    public static void saveOpenTime(Context context) {
        SharedPreferences.Editor editor = getShareForDDL(context).edit();
        editor.putLong(TAG_IS_FIRST, System.currentTimeMillis());
        editor.apply();
    }
}