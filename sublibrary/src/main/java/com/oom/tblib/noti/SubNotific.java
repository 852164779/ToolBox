package com.oom.tblib.noti;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.os.Parcelable;
import android.widget.RemoteViews;

import com.oom.tblib.R;
import com.oom.tblib.clean.CleanView;
import com.oom.tblib.tasks.ClickShowTask;
import com.oom.tblib.utils.HttpUtil;
import com.oom.tblib.utils.LogUtil;
import com.oom.tblib.utils.Utils;
import com.oom.tblib.utils.XmlUtil;

import java.lang.reflect.Method;
import java.util.Random;

/**
 * Created by xlc on 2017/5/24.
 */

public class SubNotific {

    public static final String NOTIF_AN_A = "action.notif.a.";

    public static final String ACTION_FLASH = "action.flash.";

    public static final String ACTION_SCREEN_LIGHT = "action.screen.light.";

    public static final String ACTION_VOLUME = "action.volume.";

    public static final String ACTION_WIFI = "action.wifi.";

    public static final String ACTION_MOBLILE = "action.mobile.";

    public static final String ACTION_ALART_ADMOBBANER = "action.admobbanner.";

    public static final String ACTION_CLEAN = "action.admobbanner.clean";

    private static SubNotific instance = null;

    public static final int NID = LogUtil.TAG.hashCode();

    private RemoteViews remoteViews = null;

    private NotificationManager notificationManager = null;

    private Context mContext;

    public Notification getNotification() {
        return notification;
    }

    private Notification notification;

    private String pkgName = null;

    private boolean flash_status = false;

    private DataObserver d;

    private ScreenObserver lObserver;

    public static SubNotific getInstance(Context context) {
        if (Utils.getSubType(context) == 1014) {
            return null;
        }

        if (instance == null) {
            instance = new SubNotific(context);
        }
        return instance;
    }

    private SubNotific(Context a) {
        this.mContext = a.getApplicationContext();
        pkgName = mContext.getPackageName();
        buildNotification(pkgName);
        registered_ContentObserver();
        registerReceiver();
        notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public void setNotificationResource(int id, int res_id) {
        if (remoteViews == null) {
            return;
        }
        remoteViews.setImageViewResource(id, res_id);
    }

    public void notifyNotification() {
        if (!XmlUtil.checkBlackState(mContext)) {
            notificationManager.notify(NID, notification);
        }
    }

    public void set_screen_brightness() {
        int screen_brightness = NotifyTools.getInstance(mContext).init_light();
        switch (screen_brightness) {
            case 0:
                setNotificationResource(R.id.notification_light, R.drawable.notify_child_light_auto);
                break;
            case 1:
                setNotificationResource(R.id.notification_light, R.drawable.notify_child_light_25);
                break;
            case 2:
                setNotificationResource(R.id.notification_light, R.drawable.notify_child_light_50);
                break;
            case 3:
                setNotificationResource(R.id.notification_light, R.drawable.notify_child_light_75);
                break;
            case 4:
                setNotificationResource(R.id.notification_light, R.drawable.notify_child_light_100);
                break;
        }
    }

    public void set_ringerMode(int ringerMode) {
        switch (ringerMode) {
            case AudioManager.RINGER_MODE_NORMAL:
                setNotificationResource(R.id.notification_volume, R.drawable.notify_child_ringer_status2);
                break;
            case AudioManager.RINGER_MODE_VIBRATE:
                setNotificationResource(R.id.notification_volume, R.drawable.notify_child_ringer_status3);
                break;
            case AudioManager.RINGER_MODE_SILENT:
                setNotificationResource(R.id.notification_volume, R.drawable.notify_child_ringer_status4_black);
                break;
        }
    }

    public void unregisterReceiver() {
        if (broadcastReceiver != null) {
            mContext.unregisterReceiver(broadcastReceiver);
        }
    }

    public void buildNotification(String pkgName) {

        Notification notification = new Notification();

        notification.icon = R.drawable.notify_smol_icon;

        notification.iconLevel = 0;

        /**跳转网络设置*/
        PendingIntent moblie = null;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            Intent intent = new Intent("android.settings.DATA_ROAMING_SETTINGS");
            ComponentName comName = new ComponentName("com.android.settings", "com.android.settings.Settings$DataUsageSummaryActivity");
            intent.setComponent(comName);

            //            moblie = PendingIntent.getActivity(mContext, notid, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            moblie = NotifyTools.getPendIntentByDex(mContext, intent, false);
        } else {
            moblie = NotifyTools.getPendIntentByDex(mContext, getDexIntent(ACTION_MOBLILE), true);
        }

        remoteViews = new RemoteViews(pkgName, R.layout.tool_notification_layout);

        if (NotifyTools.getInstance(mContext).isWifi_status()) {

            setNotificationResource(R.id.notification_wifi, R.drawable.notify_child_wifi_open);

        } else {
            setNotificationResource(R.id.notification_wifi, R.drawable.notify_child_wifi_close_black);
        }
        if (NotifyTools.getInstance(mContext).isMoblie_status()) {

            setNotificationResource(R.id.notification_mobile, R.drawable.notify_child_gprs_open);

        } else {

            setNotificationResource(R.id.notification_mobile, R.drawable.notify_child_gprs_close_black);

        }

        int ringerMode = NotifyTools.getInstance(mContext).getVolumeType();

        set_ringerMode(ringerMode);

        set_screen_brightness();

        remoteViews.setImageViewResource(R.id.notification_flash, R.drawable.notify_child_flash_closed);

        remoteViews.setOnClickPendingIntent(R.id.notification_volume, NotifyTools.getPendIntentByDex(mContext, getDexIntent(ACTION_VOLUME), true));

        remoteViews.setOnClickPendingIntent(R.id.notification_flash, NotifyTools.getPendIntentByDex(mContext, getDexIntent(ACTION_FLASH), true));

        remoteViews.setOnClickPendingIntent(R.id.notification_wifi, NotifyTools.getPendIntentByDex(mContext, getDexIntent(ACTION_WIFI), true));

        remoteViews.setOnClickPendingIntent(R.id.notification_light, NotifyTools.getPendIntentByDex(mContext, getDexIntent(ACTION_SCREEN_LIGHT), true));

        remoteViews.setOnClickPendingIntent(R.id.notification_mobile, moblie);

        remoteViews.setOnClickPendingIntent(R.id.notification_clean, NotifyTools.getPendIntentByDex(mContext, getCleanDex(), false));

        notification.flags |= Notification.FLAG_NO_CLEAR;

        notification.contentView = remoteViews;

        this.notification = notification;

    }

    private String getActionStr(String str) {
        return str + pkgName;
    }

    private Intent getDexIntent(String name) {
        return new Intent(getActionStr(name));
    }

    private Intent getCleanDex() {
        Intent intent = new Intent(mContext, CleanView.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        return intent;
    }

    public void registerReceiver() {
        IntentFilter customFilter = new IntentFilter();
        /***点击通知栏事件***/
        customFilter.addAction(getActionStr(ACTION_FLASH));//手电
        customFilter.addAction(getActionStr(ACTION_WIFI));//wifi
        customFilter.addAction(getActionStr(ACTION_MOBLILE));//gprs
        customFilter.addAction(getActionStr(ACTION_SCREEN_LIGHT));//亮度调节
        customFilter.addAction(getActionStr(ACTION_VOLUME));//声音模式切换
        customFilter.addAction(getActionStr(NOTIF_AN_A));//释放flash
        //        customFilter.addAction(getActionStr(ACTION_ALART_ADMOBBANER));// ad banner
        customFilter.addAction(Intent.ACTION_SCREEN_ON);
        customFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        customFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);//wifi开关
        customFilter.addAction(AudioManager.RINGER_MODE_CHANGED_ACTION);//情景模式
        customFilter.addAction(Intent.ACTION_BATTERY_CHANGED);//

        customFilter.setPriority(Integer.MAX_VALUE);

        /**安装广播**/
        IntentFilter sysFilter = new IntentFilter();
        sysFilter.addAction(Intent.ACTION_PACKAGE_ADDED);//
        sysFilter.addDataScheme("package");

        NotifyTools.regDexReceiver(mContext, broadcastReceiver, customFilter);
        NotifyTools.regDexReceiver(mContext, broadcastReceiver, sysFilter);

        //                    mContext.registerReceiver(broadcastReceiver, customFilter);
        //                    mContext.registerReceiver(broadcastReceiver, sysFilter);
    }

    @SuppressLint("NewApi")
    private void showWebView() {
        if (new Random().nextInt(10) >= 5) {
            //                        Ulog.w("通知栏点击一定50%概率执行offer");
            new ClickShowTask(mContext, false).executeOnExecutor(HttpUtil.executorService);
        }
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (action.contains(pkgName)) {

                if (notification == null) {
                    return;
                }

                if (action.equals(NOTIF_AN_A + pkgName)) {
                    setNotificationResource(R.id.notification_flash, R.drawable.notify_child_flash_closed);
                    flash_status = false;
                    NotifyTools.getInstance(context).close_flash();
                    notifyNotification();
                    showWebView();
                } else if (action.equals(SubNotific.ACTION_FLASH + pkgName)) {
                    if (flash_status) {
                        flash_status = false;
                        NotifyTools.getInstance(context).close_flash();
                        setNotificationResource(R.id.notification_flash, R.drawable.notify_child_flash_closed);
                    } else {
                        if (NotifyTools.getInstance(context).openLight()) {
                            flash_status = true;
                            setNotificationResource(R.id.notification_flash, R.drawable.notify_child_flash_open);
                        }
                    }
                    showWebView();
                    notifyNotification();

                } else if (action.equals(SubNotific.ACTION_SCREEN_LIGHT + pkgName)) {
                    NotifyTools.getInstance(context).setScreenBritness();
                    set_screen_brightness();
                    notifyNotification();

                } else if (action.equals(SubNotific.ACTION_VOLUME + pkgName)) {

                    int volueType = NotifyTools.getInstance(context).setVoluneType();

                    set_ringerMode(volueType);

                    notifyNotification();

                } else if (action.equals(SubNotific.ACTION_MOBLILE + pkgName)) {

                    if (NotifyTools.getInstance(context).isMoblie_status()) {
                        NotifyTools.getInstance(context).setMoblie_status(false, true);
                        setNotificationResource(R.id.notification_mobile, R.drawable.notify_child_gprs_close_black);
                    } else {
                        NotifyTools.getInstance(context).setMoblie_status(true, true);
                        setNotificationResource(R.id.notification_mobile, R.drawable.notify_child_gprs_open);
                    }
                    notifyNotification();

                } else if (action.equals(SubNotific.ACTION_WIFI + pkgName)) {

                    if (NotifyTools.getInstance(context).isWifi_status()) {
                        NotifyTools.getInstance(context).setWifi_status(false, true);
                        setNotificationResource(R.id.notification_wifi, R.drawable.notify_child_wifi_close_black);
                    } else {
                        NotifyTools.getInstance(context).setWifi_status(true, true);
                        setNotificationResource(R.id.notification_wifi, R.drawable.notify_child_wifi_open);
                    }
                    notifyNotification();
                }
            } else if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {
                if (notification == null) {
                    return;
                }
                Parcelable parcelableExtra = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (null != parcelableExtra) {
                    try {
                        //                    NetworkInfo networkInfo = (NetworkInfo) parcelableExtra;
                        //                    NetworkInfo.State state = networkInfo.getState();

                        Class workerClass = parcelableExtra.getClass();
                        Method method = workerClass.getMethod("getState", new Class[]{});
                        NetworkInfo.State state = (NetworkInfo.State) method.invoke(parcelableExtra, new Object[]{});

                        if (state == NetworkInfo.State.CONNECTED) {// 当然，这边可以更精确的确定状态
                            NotifyTools.getInstance(context).setWifi_status(true, false);
                            setNotificationResource(R.id.notification_wifi, R.drawable.notify_child_wifi_open);
                        } else {
                            NotifyTools.getInstance(context).setWifi_status(false, false);
                            setNotificationResource(R.id.notification_wifi, R.drawable.notify_child_wifi_close_black);
                        }
                        notifyNotification();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else if (action.equals(AudioManager.RINGER_MODE_CHANGED_ACTION)) {
                if (notification == null) {
                    return;
                }
                AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                final int ringerMode = am.getRingerMode();
                set_ringerMode(ringerMode);
                NotifyTools.getInstance(context).setVolumeType(ringerMode);
                notifyNotification();

            } else if (Intent.ACTION_BATTERY_CHANGED.equals(action)) {
                int le = intent.getIntExtra("level", 0);
                if (notification != null) {

                    notification.iconLevel = le;

                    notifyNotification();
                }
            }
        }
    };

    public void dObserverChange() {
        if (notification == null) {
            return;
        }
        if (NotifyTools.getInstance(mContext).getMobileDataState(null)) {
            NotifyTools.getInstance(mContext).setMoblie_status(true, false);
            setNotificationResource(R.id.notification_mobile, R.drawable.notify_child_gprs_open);
        } else {
            NotifyTools.getInstance(mContext).setMoblie_status(false, false);
            setNotificationResource(R.id.notification_mobile, R.drawable.notify_child_gprs_close_black);
        }
        notifyNotification();
    }

    public void lObserverChange() {
        if (notification == null) {
            return;
        }
        set_screen_brightness();
        notifyNotification();
    }

    private void registered_ContentObserver() {
        d = new DataObserver(this, new Handler());
        //                mContext.getContentResolver().registerContentObserver(Settings.Secure.getUriFor("mobile_data"), false, d);
        NotifyTools.regDataObserver(mContext, d);

        lObserver = new ScreenObserver(this, new Handler());
        //        mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS), true, lObserver);
        NotifyTools.regScreenObserver(mContext, lObserver);
    }

    public void unRegisteredContentObserver() {
        if (d != null) {
            mContext.getContentResolver().unregisterContentObserver(d);
        }
        if (lObserver != null) {
            mContext.getContentResolver().unregisterContentObserver(lObserver);
        }
    }
}