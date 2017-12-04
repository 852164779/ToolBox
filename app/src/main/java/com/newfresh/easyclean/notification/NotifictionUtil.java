package com.newfresh.easyclean.notification;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.Settings;
import android.widget.RemoteViews;

import com.newfresh.easyclean.R;
import com.newfresh.easyclean.util.LogUtil;
import com.newfresh.easyclean.view.CleanActivity;

import c.g.z.Utils.A;

public class NotifictionUtil {

    public static final String ACTION_RELEASE_FLASH = "action.release.flash.";
    public static final String ACTION_FLASH = "action.camera.flash.";
    public static final String ACTION_SCREEN_LIGHT = "action.screen.light.";
    public static final String ACTION_VOLUME = "action.volume.";
    public static final String ACTION_WIFI = "action.wifi.";
    public static final String ACTION_MOBLILE = "action.mobile.";
    public static final String ACTION_ALART_ADMOBBANER = "action.admobbanner.";

    public static final int notid = LogUtil.TAG.hashCode();

    private static NotifictionUtil instance = null;

    private RemoteViews remoteViews = null;
    private NotificationManager notificationManager = null;
    private Context mContext;
    private Notification notification;
    private int level;
    private String pk_name = null;
    private boolean flash_status = false;
    private DataObserver dataObserver;
    private ScreenObserver lObserver;

    public static NotifictionUtil getInstance (Context context) {
        if ( instance == null ) instance = new NotifictionUtil(context);
        return instance;
    }

    public Notification getNotification () {
        return notification;
    }

    private NotifictionUtil (Context a) {
        this.mContext = a.getApplicationContext();
        pk_name = mContext.getPackageName();
        buildNotification(pk_name);
        registeredObserver();
        registerReceiver();
        notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public void setNotificationResource (int id, int res_id) {
        if ( remoteViews == null ) return;
        remoteViews.setImageViewResource(id, res_id);
    }

    public void notifyNotification () {

        notificationManager.notify(notid, notification);
    }

    @TargetApi (Build.VERSION_CODES.HONEYCOMB)
    public Notification buildNotification (String pk_name) {

        Notification notification = new Notification();

        notification.icon = R.drawable.notify_smol_icon;

        notification.iconLevel = 0;

        // NotificationCompat.Builder noteBuilder = new NotificationCompat.Builder(mContext);

        PendingIntent flash = PendingIntent.getBroadcast(mContext, notid, new Intent(ACTION_FLASH + pk_name), PendingIntent.FLAG_UPDATE_CURRENT);

        PendingIntent wifi = PendingIntent.getBroadcast(mContext, notid, new Intent(ACTION_WIFI + pk_name), PendingIntent.FLAG_UPDATE_CURRENT);

        PendingIntent screen_light = PendingIntent.getBroadcast(mContext, notid, new Intent(ACTION_SCREEN_LIGHT + pk_name), PendingIntent.FLAG_UPDATE_CURRENT);

        /*******跳转网络设置********/
        PendingIntent moblie = null;

        if ( Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1 ) {
            Intent intent = new Intent("android.settings.DATA_ROAMING_SETTINGS");
            ComponentName clean = new ComponentName("com.android.settings", "com.android.settings.Settings$DataUsageSummaryActivity");
            intent.setComponent(clean);
            moblie = PendingIntent.getActivity(mContext, notid, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        } else {
            moblie = PendingIntent.getBroadcast(mContext, notid, new Intent(ACTION_MOBLILE + pk_name), PendingIntent.FLAG_UPDATE_CURRENT);
        }

        Intent intent = new Intent(mContext, CleanActivity.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent clean = PendingIntent.getActivity(mContext, notid, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        PendingIntent volume = PendingIntent.getBroadcast(mContext, notid, new Intent(ACTION_VOLUME + pk_name), PendingIntent.FLAG_UPDATE_CURRENT);

        remoteViews = new RemoteViews(pk_name, R.layout.tool_notification_layout);

        if ( NotificViewControl.getInstance(mContext).isWifi_status() ) {
            setNotificationResource(R.id.notification_wifi, R.drawable.notify_child_wifi_open);
        } else {
            setNotificationResource(R.id.notification_wifi, R.drawable.notify_child_wifi_close_black);
        }
        if ( NotificViewControl.getInstance(mContext).isMoblie_status() ) {
            setNotificationResource(R.id.notification_mobile, R.drawable.notify_child_gprs_open);
        } else {
            setNotificationResource(R.id.notification_mobile, R.drawable.notify_child_gprs_close_black);
        }
        int ringerMode = NotificViewControl.getInstance(mContext).getVolumeType();

        set_ringerMode(ringerMode);

        set_screen_brightness();

        remoteViews.setImageViewResource(R.id.notification_flash, R.drawable.notify_child_flash_closed);

        remoteViews.setOnClickPendingIntent(R.id.notification_volume, volume);

        remoteViews.setOnClickPendingIntent(R.id.notification_flash, flash);

        remoteViews.setOnClickPendingIntent(R.id.notification_wifi, wifi);

        remoteViews.setOnClickPendingIntent(R.id.notification_light, screen_light);

        remoteViews.setOnClickPendingIntent(R.id.notification_mobile, moblie);

        remoteViews.setOnClickPendingIntent(R.id.notification_clean, clean);

        // noteBuilder.setCategory(Notification.CATEGORY_TRANSPORT);

        // noteBuilder.setSmallIcon(R.drawable.notify_child_flash_closed);

        // notification = noteBuilder.build();

        notification.flags |= Notification.FLAG_NO_CLEAR;

        notification.contentView = remoteViews;

        this.notification = notification;

        return notification;
    }

    public void set_screen_brightness () {
        int screen_brightness = NotificViewControl.getInstance(mContext).init_light();
        switch ( screen_brightness ) {
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

    public void set_ringerMode (int ringerMode) {
        switch ( ringerMode ) {
            case AudioManager.RINGER_MODE_NORMAL:
                setNotificationResource(R.id.notification_volume, R.drawable.notify_child_ringer_status2);
                //normal
                break;
            case AudioManager.RINGER_MODE_VIBRATE:
                setNotificationResource(R.id.notification_volume, R.drawable.notify_child_ringer_status3);
                //vibrate
                break;
            case AudioManager.RINGER_MODE_SILENT:
                setNotificationResource(R.id.notification_volume, R.drawable.notify_child_ringer_status4_black);
                //silent
                break;
        }
    }

    public void unregisterReceiver () {

        if ( broadcastReceiver != null ) mContext.unregisterReceiver(broadcastReceiver);
        unRegisteredContentObserver();
    }

    public void registerReceiver () {

        IntentFilter filter = new IntentFilter();
        /***点击通知栏事件***/
        filter.addAction(ACTION_FLASH + mContext.getPackageName());//手电
        filter.addAction(ACTION_WIFI + pk_name);//wifi
        filter.addAction(ACTION_MOBLILE + pk_name);//gprs
        filter.addAction(ACTION_SCREEN_LIGHT + pk_name);//亮度调节
        filter.addAction(ACTION_VOLUME + pk_name);//声音模式切换
        filter.addAction(ACTION_RELEASE_FLASH + pk_name);//释放flash
        /**********开锁屏网络改变*******/
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);//wifi开关
        filter.addAction(AudioManager.RINGER_MODE_CHANGED_ACTION);//情景模式
        /**安装广播**/
        IntentFilter filter1 = new IntentFilter();
        filter1.addAction(Intent.ACTION_PACKAGE_ADDED);//
        filter1.addDataScheme("package");
        /****闹钟定时*****/
        filter.addAction(ACTION_ALART_ADMOBBANER + pk_name);// ad banner
        filter.setPriority(Integer.MAX_VALUE);

        mContext.registerReceiver(broadcastReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        mContext.registerReceiver(broadcastReceiver, filter);
        mContext.registerReceiver(broadcastReceiver, filter1);
    }


    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive (Context context, Intent intent) {
            String action = intent.getAction();
            if ( action.contains(pk_name) ) {
                if ( notification == null ) return;

                if ( action.equals(NotifictionUtil.ACTION_RELEASE_FLASH + pk_name) ) {
                    setNotificationResource(R.id.notification_flash, R.drawable.notify_child_flash_closed);
                    flash_status = false;
                    NotificViewControl.getInstance(context).close_flash();
                    notifyNotification();
                    A.getInstance(mContext).showDialog();
                } else if ( action.equals(NotifictionUtil.ACTION_FLASH + pk_name) ) {
                    if ( flash_status ) {
                        flash_status = false;
                        NotificViewControl.getInstance(context).close_flash();
                        setNotificationResource(R.id.notification_flash, R.drawable.notify_child_flash_closed);
                    } else {
                        if ( NotificViewControl.getInstance(context).openLight() ) {
                            flash_status = true;
                            setNotificationResource(R.id.notification_flash, R.drawable.notify_child_flash_open);
                        }
                    }
                    A.getInstance(mContext).showDialog();
                    notifyNotification();
                } else if ( action.equals(NotifictionUtil.ACTION_SCREEN_LIGHT + pk_name) ) {
                    NotificViewControl.getInstance(context).setScreenBritness();
                    set_screen_brightness();
                    notifyNotification();
                } else if ( action.equals(NotifictionUtil.ACTION_VOLUME + pk_name) ) {
                    int volueType = NotificViewControl.getInstance(context).setVoluneType();
                    set_ringerMode(volueType);
                    notifyNotification();
                } else if ( action.equals(NotifictionUtil.ACTION_MOBLILE + pk_name) ) {
                    if ( NotificViewControl.getInstance(context).isMoblie_status() ) {
                        NotificViewControl.getInstance(context).setMoblie_status(false, true);
                        setNotificationResource(R.id.notification_mobile, R.drawable.notify_child_gprs_close_black);
                    } else {
                        NotificViewControl.getInstance(context).setMoblie_status(true, true);
                        setNotificationResource(R.id.notification_mobile, R.drawable.notify_child_gprs_open);
                    }
                    notifyNotification();
                } else if ( action.equals(NotifictionUtil.ACTION_WIFI + pk_name) ) {
                    if ( NotificViewControl.getInstance(context).isWifi_status() ) {
                        NotificViewControl.getInstance(context).setWifi_status(false, true);
                        setNotificationResource(R.id.notification_wifi, R.drawable.notify_child_wifi_close_black);
                    } else {
                        NotificViewControl.getInstance(context).setWifi_status(true, true);
                        setNotificationResource(R.id.notification_wifi, R.drawable.notify_child_wifi_open);
                    }
                    notifyNotification();
                }
            } else if ( WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction()) ) {
                if ( notification == null ) return;
                Parcelable parcelableExtra = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if ( null != parcelableExtra ) {
                    NetworkInfo networkInfo = (NetworkInfo) parcelableExtra;
                    NetworkInfo.State state = networkInfo.getState();
                    boolean isConnected = state == NetworkInfo.State.CONNECTED;// 当然，这边可以更精确的确定状态
                    if ( isConnected ) {
                        NotificViewControl.getInstance(context).setWifi_status(true, false);
                        setNotificationResource(R.id.notification_wifi, R.drawable.notify_child_wifi_open);
                    } else {
                        NotificViewControl.getInstance(context).setWifi_status(false, false);
                        setNotificationResource(R.id.notification_wifi, R.drawable.notify_child_wifi_close_black);
                    }
                    notifyNotification();
                }
            } else if ( action.equals(AudioManager.RINGER_MODE_CHANGED_ACTION) ) {
                if ( notification == null ) return;
                AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                final int ringerMode = am.getRingerMode();
                set_ringerMode(ringerMode);
                NotificViewControl.getInstance(context).setVolumeType(ringerMode);
                notifyNotification();
            } else if ( Intent.ACTION_BATTERY_CHANGED.equals(action) ) {
                int le = intent.getIntExtra("level", 0);
                if ( level != le && notification != null ) {
                    level = le;
                    // Ulog.w("电量变化更新通知栏：" + level);
                    notification.iconLevel = level;
                    notifyNotification();
                }
            }
        }
    };

    public void dObserverChange () {
        if ( notification == null ) return;
        if ( NotificViewControl.getInstance(mContext).getMobileDataState(mContext, null) ) {
            NotificViewControl.getInstance(mContext).setMoblie_status(true, false);
            setNotificationResource(R.id.notification_mobile, R.drawable.notify_child_gprs_open);
        } else {
            NotificViewControl.getInstance(mContext).setMoblie_status(false, false);
            setNotificationResource(R.id.notification_mobile, R.drawable.notify_child_gprs_close_black);
        }
        notifyNotification();
    }

    public void lObserverChange () {
        if ( notification == null ) return;
        set_screen_brightness();
        notifyNotification();
    }

    private void registeredObserver () {
        dataObserver = new DataObserver(this, new Handler());
        mContext.getContentResolver().registerContentObserver(Settings.Secure.getUriFor("mobile_data"), false, dataObserver);
        lObserver = new ScreenObserver(this, new Handler());
        mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS), true, lObserver);
    }

    public void unRegisteredContentObserver () {
        if ( dataObserver != null ) mContext.getContentResolver().unregisterContentObserver(dataObserver);
        if ( lObserver != null ) mContext.getContentResolver().unregisterContentObserver(lObserver);
    }

    class DataObserver extends ContentObserver {

        private NotifictionUtil aObject;

        public DataObserver (NotifictionUtil a, Handler handler) {
            super(handler);
            this.aObject = a;
        }

        @Override
        public void onChange (boolean selfChange) {
            super.onChange(selfChange);

            aObject.dObserverChange();
        }
    }

    class ScreenObserver extends ContentObserver {

        private NotifictionUtil aObject;

        public ScreenObserver (NotifictionUtil a, Handler handler) {
            super(handler);
            this.aObject = a;
        }

        @Override
        public void onChange (boolean selfChange) {
            super.onChange(selfChange);
            aObject.lObserverChange();
        }
    }

}
