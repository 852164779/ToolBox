package test.com.androidtest.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.FeatureInfo;
import android.content.pm.PackageManager;
import android.view.View;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Utils {

    public static final String ACTION_RELEASE_FLASH = "action.release_flash.";

    public static ExecutorService executorService = Executors.newScheduledThreadPool(20);

    public static void save_sleep_time(Context context, int vules) {
        SharedPreferences preferences = context.getSharedPreferences("sleep_time_xml", 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("sleep_time", vules);
        editor.apply();
    }

    public static int get_sleep_time(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("sleep_time_xml", 0);
        return preferences.getInt("sleep_time", 60000);
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


    public static void hideNaviga(Activity context) {
        context.getWindow().getDecorView().setSystemUiVisibility(//
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }
}