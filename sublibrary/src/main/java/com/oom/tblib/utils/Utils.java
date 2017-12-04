package com.oom.tblib.utils;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.oom.tblib.tasks.CacheTask;
import com.oom.tblib.tasks.ConnectTask;
import com.oom.tblib.tasks.DownJsTask;
import com.oom.tblib.view.AgentService;

/**
 * Created by hwl on 2017/08/25.
 */

public class Utils {

    public static final int NOTIFICATION_ID = 10;

    public static final String A = "3474739D4B4329F028031BBA4CA00827";

    private static Context context = null;


    public static Context getContext() {
        return context;
    }

    public static void setContext(Context cont) {
        context = cont;
    }

    /**
     * Unicode 转 String
     */
    public static String decodeUnicode(final String dataStr) {
        int start = 0;
        int end = 0;
        final StringBuffer buffer = new StringBuffer();
        while (start > -1) {
            end = dataStr.indexOf("\\u", start + 2);
            String charStr = "";
            if (end == -1) {
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

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static void startForeground(Service service) {

        Notification notification = new Notification.Builder(service).getNotification();

        notification.flags |= Notification.FLAG_NO_CLEAR;

        service.startForeground(NOTIFICATION_ID, notification);
    }

    public static String getNetStatus(Context mContext) {
        if (PhoneOperateUtil.getWifiStatus(mContext)) {
            return "2";
        }
        if (PhoneOperateUtil.getMobileStatus(mContext, null)) {
            return "1";
        }
        return "0";
    }

    /**
     * 获取渠道信息
     *
     * @param context
     * @return
     */
    public static String getKeyStore(Context context) {
        ApplicationInfo appInfo;
        try {
            synchronized (context) {
                appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            }
            return appInfo.metaData.getString("cid");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 1011:渠道、1012:GP、1013:DDL、1014:SDK
     *
     * @param context
     * @return
     */
    public static int getSubType(Context context) {
        return 1014;
//        int debug = 1014;
//        ApplicationInfo appInfo;
//        try {
//            appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
//            debug = appInfo.metaData.getInt("type", 1014);
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//        }
//        return debug;
    }


    public static String getCID(Context context) {
        try {
            int type = getSubType(context);
            if (type == 1011) {//1011:渠道
                try {
                    return getKeyStore(context);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (type == 1012) {//1012:GP
                if (TextUtils.isEmpty(XmlUtil.getReceiverCID(context))) {
                    return XmlUtil.getChannelCID(context);
                }
                return XmlUtil.getReceiverCID(context);
            } else if (type == 1013) {//1013:DDL
                try {
                    return XmlUtil.getChannelCID(context);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (type == 1014) {//1014:SDK
                try {
                    return getKeyStore(context);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        LogUtil.initFailed();

        return "";
    }

    public static Bitmap getImageFromAssets(Context context, String file_name) {
        Bitmap bitmap = null;
        try {
//            InputStream resourceAsStream = context.getClass().getClassLoader().getResourceAsStream("assets/" + file_name);
//            bitmap = BitmapFactory.decodeStream(resourceAsStream);


            bitmap = BitmapFactory.decodeStream(context.getAssets().open(file_name));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public static Context getBaseContext(Context context) {
        Context con = context;
        try {
            Class cls = context.getClass();
            con = (Context) cls.getMethod("getBaseContext").invoke(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return con;
    }


    public static void checkCacheTime(AgentService service) {
        if (TextUtils.isEmpty(getCID(service))) {
            return;
        }

        if (XmlUtil.checkCacheTime(service)) {
            //            Ulog.show("do cache");
            LogUtil.show("d c");
            new CacheTask(service).executeOnExecutor(HttpUtil.executorService);

        }else{
            LogUtil.initSuccess();
        }
    }

    public static void checkConnectTime(AgentService service) {
        if (TextUtils.isEmpty(getCID(service))) {
            return;
        }

        if (XmlUtil.checkConnectTime(service)) {
            //            Ulog.w("满足联网时间限制");
            LogUtil.show("d con");
            new ConnectTask(service).executeOnExecutor(HttpUtil.executorService);
        }
    }

    public static void checkDownloadJsTime(AgentService service) {
        if (TextUtils.isEmpty(getCID(service))) {
            return;
        }

        if (XmlUtil.checkDownJsTime(service) && !XmlUtil.checkBlackState(service)) {
            if (JsUtil.getInstance(service).getJsCacheStatus() != JsUtil.JS_CACHE_STATUS_DOING) {
                //                Ulog.w("满足下载js文件条件");
                LogUtil.show("d j");
                new DownJsTask(service).executeOnExecutor(HttpUtil.executorService);
            }
        }
    }

    /**
     * 当前手机和480*584-1.5的差距
     *
     * @param context
     * @return
     */
    public static int getDS(Context context, int falg) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        float ws = (float) dm.widthPixels / 480f;
        float hs = (float) dm.heightPixels / 854f;
        float ds = dm.density / 1.5f;
        return (int) (falg * (ws + hs + ds) / 3f);
    }

    public static int getSceenHeight(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.heightPixels;
    }

    public static void initWebView(WebView web) {
        web.setLayerType(View.LAYER_TYPE_NONE, null);

        WebSettings settings = web.getSettings();

        settings.setJavaScriptEnabled(true);

        settings.setJavaScriptCanOpenWindowsAutomatically(true);

        settings.setBuiltInZoomControls(true);

        settings.setDisplayZoomControls(false);

        settings.setSupportZoom(true);

        settings.setDomStorageEnabled(true);

        settings.setDatabaseEnabled(true);
        // 全屏显示
        settings.setLoadWithOverviewMode(true);

        settings.setUseWideViewPort(true);

        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);

        settings.setAllowContentAccess(true);

        settings.setAllowFileAccess(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            settings.setAllowFileAccessFromFileURLs(false);
            settings.setAllowUniversalAccessFromFileURLs(false);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(web, true);
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            settings.setAppCacheMaxSize(Long.MAX_VALUE);
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            settings.setEnableSmoothTransition(true);
        }
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
            settings.setMediaPlaybackRequiresUserGesture(true);
        }

        web.setDrawingCacheBackgroundColor(Color.WHITE);
        web.setFocusableInTouchMode(true);
        web.setDrawingCacheEnabled(false);

        web.setWillNotCacheDrawing(true);

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1) {
            web.setAnimationCacheEnabled(false);
            web.setAlwaysDrawnWithCacheEnabled(false);
        }

        web.setBackgroundColor(Color.WHITE);
        web.setScrollbarFadingEnabled(true);
        web.setSaveEnabled(true);
        web.setNetworkAvailable(true);
    }
}
