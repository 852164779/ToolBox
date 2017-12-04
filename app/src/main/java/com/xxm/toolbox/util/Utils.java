package com.xxm.toolbox.util;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.FeatureInfo;
import android.content.pm.PackageManager;

public class Utils {

    public static final String ACTION_RELEASE_FLASH = "action.release_flash.";

    public static final String SHARE_NAME = "resource.status.share";
    private static final String TAG_SLEEP = "tag.toolbox.sleep.int";
    public static final String TAG_BLACK = "tag.sub.black.boolean";

    public static SharedPreferences getShare(Context context) {
        return context.getSharedPreferences(SHARE_NAME, 0);
    }

    public static void save_sleep_time(Context context, int vules) {
        SharedPreferences.Editor editor = getShare(context).edit();
        editor.putInt(TAG_SLEEP, vules);
        editor.apply();
    }

    public static int get_sleep_time(Context context) {
        return getShare(context).getInt(TAG_SLEEP, 60000);
    }

    public static boolean hasFlash(Context context) {
        PackageManager pm = context.getPackageManager();
        FeatureInfo[] featureInfos = pm.getSystemAvailableFeatures();
        for (FeatureInfo f : featureInfos) {
            if (PackageManager.FEATURE_CAMERA_FLASH.equals(f.name)) {
                return true;
            }
        }
        return false;
    }

    public static void releas_flash_(Context context) {
        Intent intent = new Intent();
        intent.setAction(ACTION_RELEASE_FLASH + context.getPackageName());
        context.sendBroadcast(intent);
    }

    public static int getSubType(Context context) {
        int debug = 1014;
        ApplicationInfo appInfo;
        try {
            appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            debug = appInfo.metaData.getInt("type", 1014);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return debug;
    }

    public static void saveBlackState(Context context, boolean org) {
        SharedPreferences.Editor editor = getShare(context).edit();
        editor.putBoolean(TAG_BLACK, org);
        editor.apply();
    }

    public static boolean checkBlackState(Context context) {
        return getShare(context).getBoolean(TAG_BLACK, false);
    }

}