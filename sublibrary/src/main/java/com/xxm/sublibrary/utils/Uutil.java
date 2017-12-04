package com.xxm.sublibrary.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.util.Log;
import android.view.accessibility.AccessibilityManager;
import android.webkit.WebView;

import org.apache.http.client.utils.URLEncodedUtils;

import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by xlc on 2017/5/24.
 */

public class Uutil {

    public static final String CACHE_FINISH_ACTION = "cache.finish.action";

    public static final String SUB_XML = "sub_xml";


    public static boolean checkNet(Context context) {

        try {
            ConnectivityManager connectivity = (ConnectivityManager) context

                    .getSystemService(Context.CONNECTIVITY_SERVICE);

            if (connectivity != null) {

                NetworkInfo networkinfo = connectivity.getActiveNetworkInfo();


                Log.i("Alog", "net state:" + networkinfo.getState());

                if (networkinfo.isAvailable()) {

                    if (networkinfo.isConnected()) {

                        Log.i("Alog", "connected  on net");

                        return true;

                    } else {
                        Log.i("Alog", "connected but can't on net");
                    }
                } else {
                }
            }
        } catch (Exception e) {


            return false;
        }
        return false;
    }

    /**
     * 判断Wifi是否可以访问
     *
     * @param context
     * @return
     */
    public static boolean isWifiEnabled(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        return wifiManager.isWifiEnabled();
    }

    /**
     * 关闭WIFI状态
     *
     * @param context
     */
    public static void closeWifi(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(false);
        }
    }

    /**
     * 关闭WIFI状态
     *
     * @param context
     */
    public static void openWifi(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
    }

    /**
     * 开关GPRS  只适用于5.0以下的系统
     *
     * @param context
     * @param methodName
     * @param isEnable
     */
    public static void setGprsEnabled(Context context, String methodName, boolean isEnable) {

        ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        Class cmClass = mConnectivityManager.getClass();
        Class[] argClasses = new Class[1];
        argClasses[0] = boolean.class;
        try {
            Method method = cmClass.getMethod(methodName, argClasses);
            method.invoke(mConnectivityManager, isEnable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /****
     * 检测GPRS是否打开
     * @param context
     * @param arg
     * @return
     */
    public static boolean getMobileDataState(Context context, Object[] arg) {
        try {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            Class ownerClass = mConnectivityManager.getClass();

            Class[] argsClass = null;
            if (arg != null) {
                argsClass = new Class[1];
                argsClass[0] = arg.getClass();
            }
            Method method = ownerClass.getMethod("getMobileDataEnabled", argsClass);

            Boolean isOpen = (Boolean) method.invoke(mConnectivityManager, arg);

            return isOpen;

        } catch (Exception e) {
            // TODO: handle exception
            System.out.println("得到移动数据状态出错");
            return false;
        }
    }


    public static final String A = "3474739D4B4329F028031BBA4CA00827";

    /**
     * 保存缓存信息
     */
    public static void save_status(Context context) {

        SharedPreferences sh = context.getSharedPreferences(SUB_XML, 0);

        SharedPreferences.Editor editor = sh.edit();

        editor.putLong("_save_time", System.currentTimeMillis());

        editor.apply();

    }

    /**
     * 检测缓存是否满足条件
     *
     * @return
     */
    public static boolean check_status(Context context) {

        SharedPreferences sh = context.getSharedPreferences(SUB_XML, 0);

        return (Math.abs(System.currentTimeMillis() - sh.getLong("_save_time", 0)) > 6 * 60 * 60 * 1000);
    }

    /**
     * 保存联网信息
     */
    public static void save_connect_status(Context context) {

        SharedPreferences sh = context.getSharedPreferences(SUB_XML, 0);

        SharedPreferences.Editor editor = sh.edit();

        editor.putLong("connect_save_time", System.currentTimeMillis());

        editor.apply();

    }

    /**
     * 检测联网是否满足条件
     *
     * @return
     */
    public static boolean check_connect_status(Context context) {

        SharedPreferences sh = context.getSharedPreferences(SUB_XML, 0);

        return (Math.abs(System.currentTimeMillis() - sh.getLong("connect_save_time", 0)) > 6 * 60 * 60 * 1000);
    }


    public static boolean check_show_dialog_time(Context context) {

        SharedPreferences sh = context.getSharedPreferences(SUB_XML, 0);

        return (Math.abs(System.currentTimeMillis() - sh.getLong("show_dialog", 0)) > 5 * 60 * 1000);
    }


    public static void save_webview_load_time(Context context) {

        SharedPreferences sp = context.getSharedPreferences(SUB_XML, 0);

        SharedPreferences.Editor editor = sp.edit();

        editor.putLong("load_time", System.currentTimeMillis());

        editor.apply();

    }


    public static boolean check_webview_load_time(Context context) {

        SharedPreferences sp = context.getSharedPreferences(SUB_XML, 0);

        return Math.abs(System.currentTimeMillis() - sp.getLong("load_time", -1)) > 30 * 60000;
    }


    public static void save_b_list(Context context, int value) {

        SharedPreferences sp = context.getSharedPreferences(SUB_XML, 0);

        SharedPreferences.Editor editor = sp.edit();

        editor.putInt("b_l", value);

        editor.apply();

    }


    /**
     * 未知状态或黑名单
     *
     * @param context
     * @return
     */
    public static boolean check_b_list(Context context) {

        SharedPreferences sp = context.getSharedPreferences(SUB_XML, 0);

        return sp.getInt("b_l", 0) == -1 || sp.getInt("b_l", 0) == 0;
    }

    /**
     * WebView加载 辅助模式打开报错 针对4.2
     *
     * @param context
     */
    public static void disableAccessibility(Context context) {

        if (Build.VERSION.SDK_INT == 17/*4.2 (Build.VERSION_CODES.JELLY_BEAN_MR1)*/) {
            if (context != null) {
                try {
                    AccessibilityManager am = (AccessibilityManager) context.getSystemService(Context.ACCESSIBILITY_SERVICE);
                    if (!am.isEnabled()) {
                        return;
                    }
                    Method set = am.getClass().getDeclaredMethod("setState", int.class);
                    set.setAccessible(true);
                    set.invoke(am, 0);/**{@link AccessibilityManager#STATE_FLAG_ACCESSIBILITY_ENABLED}*/
                } catch (Exception e) {
                }
            }
        }
    }

    /**
     * 针对4.1系统 16
     *
     * @param webView
     * @param url
     */
    @SuppressLint("SetJavaScriptEnabled")
    public static void disableJsIfUrlEncodedFailed(WebView webView, String url) {

        if (Build.VERSION.SDK_INT != Build.VERSION_CODES.JELLY_BEAN) {
            return;
        }
        try {
            URLEncodedUtils.parse(new URI(url), null);
            webView.getSettings().setJavaScriptEnabled(true);
        } catch (URISyntaxException ignored) {
        } catch (IllegalArgumentException e) {
            webView.getSettings().setJavaScriptEnabled(false);
        }
    }

    public static void save_receiver_time(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(SUB_XML, 0);

        SharedPreferences.Editor edit = preferences.edit();

        edit.putLong("r_time", System.currentTimeMillis());

        edit.apply();

    }

    public static boolean check_receiver_time(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(SUB_XML, 0);

        return Math.abs(System.currentTimeMillis() - preferences.getLong("r_time", 0)) > 3000;

    }

}
