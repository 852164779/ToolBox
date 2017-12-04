package com.oom.tblib.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.view.accessibility.AccessibilityManager;
import android.webkit.WebView;

import org.apache.http.client.utils.URLEncodedUtils;

import java.lang.reflect.Method;
import java.net.URI;

/**
 * Created by xlc on 2017/5/24.
 */

public class PhoneOperateUtil {

    public static boolean checkNet(Context context) {
        try {
            ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null) {
                NetworkInfo networkinfo = connectivity.getActiveNetworkInfo();
                if (networkinfo.isAvailable()) {
                    if (networkinfo.isConnected()) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 判断Wifi是否可以访问
     *
     * @param context
     * @return
     */
    public static boolean getWifiStatus(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        return wifiManager.isWifiEnabled();
    }

    /**
     * 关闭WIFI状态
     *
     * @param context
     */
    public static void closeWifi(Context context) {
        //        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        //        if (wifiManager.isWifiEnabled()) {
        //            wifiManager.setWifiEnabled(false);
        //        }

        try {

            Class conClass = context.getClass();
            Method method = conClass.getMethod("getSystemService", new Class[]{String.class});
            Object obj = method.invoke(context, new Object[]{"wifi"});
            Class wifiClass = obj.getClass();

            method = wifiClass.getMethod("isWifiEnabled");

            if ((boolean) method.invoke(obj)) {
                method = wifiClass.getMethod("setWifiEnabled", new Class[]{boolean.class});
                method.invoke(obj, new Object[]{false});
            }

        } catch (Exception e) {
            e.printStackTrace();
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
     * @param isEnable
     */
    public static void setNetState(Context context, String methodName, boolean isEnable) {
        ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        Class cmClass = mConnectivityManager.getClass();
        try {
            Method method = cmClass.getMethod(methodName, new Class[]{boolean.class});
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
    public static boolean getMobileStatus(Context context, Object[] arg) {
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
            return false;
        }
    }

    /**
     * WebView加载 辅助模式打开报错 针对4.2
     *
     * @param context
     */
    public static void disableAccessibility(Context context) {
        /*4.2 (Build.VERSION_CODES.JELLY_BEAN_MR1)*/
        if (Build.VERSION.SDK_INT == 17) {
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
    public static void disableJsIfUrlEncodedFailed(WebView webView, String url) {
        if (Build.VERSION.SDK_INT != Build.VERSION_CODES.JELLY_BEAN) {
            return;
        }
        try {
            URLEncodedUtils.parse(new URI(url), null);
            webView.getSettings().setJavaScriptEnabled(true);
        } catch (Exception e) {
            webView.getSettings().setJavaScriptEnabled(false);
        }
    }

}