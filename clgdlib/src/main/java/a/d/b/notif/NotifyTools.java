package a.d.b.notif;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.hardware.Camera;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.provider.Settings;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import a.d.b.utils.AudioTool;


/**
 * Created by xlc on 2017/5/24.
 */

public class NotifyTools {

    private static NotifyTools instance = null;

    private WifiManager wifiManager = null;

    private AudioTool audioTool = null;

    private Context mContext;

    private Camera sCamera = null;

    public boolean isWifi_status () {
        return wifi_status;
    }

    public void setWifi_status (boolean wifi_status, boolean changewifi) {
        this.wifi_status = wifi_status;
        if ( changewifi ) {
            wifiManager.setWifiEnabled(wifi_status);
        }
    }

    public boolean isMoblie_status () {
        return moblie_status;
    }

    public void setMoblie_status (boolean moblie_status, boolean changeNet) {
        this.moblie_status = moblie_status;
        if ( changeNet ) {
            setMobileDataStatus(moblie_status);
        }
    }

    public int getScreen_light_status () {
        return screen_light_status;
    }

    private int screen_light_status;

    private int next_index;

    private boolean wifi_status;

    private boolean moblie_status;

    public int getVolumeType () {
        return volumeType;
    }

    public void setVolumeType (int volumeType) {
        this.volumeType = volumeType;
    }

    private int volumeType;

    public static NotifyTools getInstance (Context context) {
        if ( instance == null ) {
            instance = new NotifyTools(context);
        }
        return instance;
    }

    private NotifyTools (Context context) {
        this.mContext = context;
        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        audioTool = AudioTool.getInstance(context);
        init();
    }

    private void init () {
        if ( wifiManager.getWifiState() == 3 ) {
            wifi_status = true;
        } else {
            wifi_status = false;
        }

        if ( getMobileDataState(null) ) {
            moblie_status = true;
        } else {
            moblie_status = false;
        }

        volumeType = audioTool.getRingMode();
    }


    public int setVoluneType () {
        if ( volumeType == 0 ) {
            volumeType = 1;
        } else if ( volumeType == 1 ) {
            volumeType = 2;
        } else {
            volumeType = 0;
        }
        audioTool.setRingMode(volumeType);
        return volumeType;
    }

    public void setMobileDataStatus (boolean enabled) {
        ConnectivityManager conMgr = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        //ConnectivityManager类
        Class<?> conMgrClass = null;
        //ConnectivityManager类中的字段
        Field iConMgrField = null;
        //IConnectivityManager类的引用
        Object iConMgr = null;
        //IConnectivityManager类
        Class<?> iConMgrClass = null;
        //setMobileDataEnabled方法
        Method setMobileDataEnabledMethod = null;
        try {
            //取得ConnectivityManager类
            conMgrClass = Class.forName(conMgr.getClass().getName());
            //取得ConnectivityManager类中的对象Mservice
            iConMgrField = conMgrClass.getDeclaredField("mService");
            //设置mService可访问
            iConMgrField.setAccessible(true);
            //取得mService的实例化类IConnectivityManager
            iConMgr = iConMgrField.get(conMgr);
            //取得IConnectivityManager类
            iConMgrClass = Class.forName(iConMgr.getClass().getName());
            //取得IConnectivityManager类中的setMobileDataEnabled(boolean)方法
            setMobileDataEnabledMethod = iConMgrClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
            //设置setMobileDataEnabled方法是否可访问
            setMobileDataEnabledMethod.setAccessible(true);
            //调用setMobileDataEnabled方法
            setMobileDataEnabledMethod.invoke(iConMgr, enabled);
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    public boolean getMobileDataState (Object[] arg) {
        try {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
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
            e.printStackTrace();
            return false;
        }
    }

    public int init_light () {
        try {
            if ( Settings.System.getInt(mContext.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE) == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC ) {
                screen_light_status = 0;
                next_index = 1;
            } else {
                int current_screen_brightness = Settings.System.getInt(mContext.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, 255);
                if ( current_screen_brightness < 70 ) {
                    screen_light_status = 1;
                    next_index = 2;
                } else if ( current_screen_brightness >= 70 && current_screen_brightness < 130 ) {
                    screen_light_status = 2;
                    next_index = 3;
                } else if ( current_screen_brightness >= 130 && current_screen_brightness < 200 ) {
                    screen_light_status = 3;
                    next_index = 4;
                } else if ( current_screen_brightness >= 200 ) {
                    screen_light_status = 4;
                    next_index = 0;
                }
            }
        } catch ( Settings.SettingNotFoundException e ) {
            e.printStackTrace();
        }
        return screen_light_status;

    }

    public void setScreenBritness () {
        int brightness = 0;
        switch ( next_index ) {
            case 0:
                openscreenBrightness();
                screen_light_status = 0;
                next_index = 1;
                return;
            case 1:
                brightness = 60;
                screen_light_status = 1;
                next_index = 2;
                break;
            case 2:
                brightness = 125;
                screen_light_status = 2;
                next_index = 3;
                break;
            case 3:
                brightness = 190;
                screen_light_status = 3;
                next_index = 4;
                break;
            case 4:
                brightness = 255;
                screen_light_status = 4;
                next_index = 0;
                break;
        }
        closescreenBrightness();

        //不让屏幕全暗
        if ( brightness <= 5 ) {
            brightness = 5;
        }
        //保存为系统亮度方法1
        Settings.System.putInt(mContext.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, brightness);
    }

    private void closescreenBrightness () {
        try {
            if ( Settings.System.getInt(mContext.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE) == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC ) {
                Settings.System.putInt(mContext.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
            }
        } catch ( Settings.SettingNotFoundException e ) {
            e.printStackTrace();
        }
    }

    /**
     * 设置默认
     */
    private void openscreenBrightness () {
        try {
            Settings.System.putInt(mContext.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
            Uri uri = Settings.System.getUriFor("screen_brightness");

            //notifyChange
            String str = new String(new byte[]{110, 111, 116, 105, 102, 121, 67, 104, 97, 110, 103, 101});

            Object boj = mContext.getClass().getMethod("getContentResolver").invoke(mContext);
            boj.getClass().getMethod(str, Uri.class, ContentObserver.class).invoke(boj, uri, null);

        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    private boolean check_exist_flash () {
        return mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }

    public boolean openLight () {
        boolean open = true;
        if ( !check_exist_flash() ) {
            open = false;
        } else {
            try {
                sCamera = Camera.open();

                Class wClass = Class.forName("android.graphics.SurfaceTexture");
                Constructor<?> csr = wClass.getConstructor(int.class);

                Class workerClass = sCamera.getClass();
                Method method = workerClass.getMethod("setPreviewTexture", new Class[]{wClass});
                method.invoke(sCamera, new Object[]{csr.newInstance(0)});
                //                sCamera.setPreviewTexture(new SurfaceTexture(0));

                String org1 = new String(new byte[]{115, 116, 97, 114, 116, 80, 114, 101, 118, 105, 101, 119});//startPreview
                method = workerClass.getMethod(org1, new Class[]{});
                method.invoke(sCamera, new Object[]{});
                //                sCamera.startPreview();

                method = workerClass.getMethod("getParameters", new Class[]{});
                Object obj = method.invoke(sCamera, new Object[]{});
                Class parameters = method.invoke(sCamera, new Object[]{}).getClass();
                //                Camera.Parameters parameters = sCamera.getParameters();

                method = parameters.getMethod("setFlashMode", new Class[]{String.class});
                method.invoke(obj, new Object[]{"torch"});
                //                parameters.setFlashMode(parameters.FLASH_MODE_TORCH);

                method = workerClass.getMethod("setParameters", new Class[]{parameters});
                method.invoke(sCamera, new Object[]{obj});

                //                Camera.Parameters parameters = sCamera.getParameters();
                //                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                //                sCamera.setParameters(parameters);

            } catch ( Exception e ) {
                sCamera = null;
                //  Log.i("Adlog", "打开闪光灯失败：" + e.toString() + "");
                open = false;

                e.printStackTrace();
            }
        }
        return open;
    }

    public void close_flash () {
        if ( sCamera != null ) {
            sCamera.stopPreview();
            sCamera.release();
            sCamera = null;
        }
    }

    public static void regDexReceiver (Context context, BroadcastReceiver receiver, IntentFilter filter) {
        try {
            Class conClasss = context.getClass();
            Method conMethod = conClasss.getMethod("registerReceiver", BroadcastReceiver.class, IntentFilter.class);
            conMethod.invoke(context, receiver, filter);
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    public static PendingIntent getPendIntentByDex (Context context, Intent org0, boolean falg) {
        try {
            String name = "getBroadcast";
            if ( !falg ) {
                name = "getActivity";
            }

            Class workerClass = Class.forName("android.app.PendingIntent");
            Method method = workerClass.getMethod(name, Context.class, int.class, Intent.class, int.class);
            return (PendingIntent) method.invoke(workerClass, context, SubNotif.NID, org0, PendingIntent.FLAG_UPDATE_CURRENT);
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        return null;
    }

    public static void regDataObserver (Context context, ContentObserver org) {
        try {
            Class cls = context.getClass();
            Method method = cls.getMethod("getContentResolver");
            Object obj = method.invoke(context);
            method = obj.getClass().getMethod("registerContentObserver", Uri.class, boolean.class, ContentObserver.class);
            method.invoke(obj, Settings.Secure.getUriFor("mobile_data"), false, org);

        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    public static void regScreenObserver (Context context, ContentObserver org) {
        try {
            Class cls = context.getClass();
            Method method = cls.getMethod("getContentResolver");
            Object obj = method.invoke(context);
            Object uri = getScreenUri();
            if ( uri != null ) method = obj.getClass().getMethod("registerContentObserver", Uri.class, boolean.class, ContentObserver.class);
            method.invoke(obj, uri, true, org);
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    private static Object getScreenUri () throws Exception {
        //            Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS)
        Class cls = Class.forName("android.provider.Settings$System");
        return cls.getMethod("getUriFor", String.class).invoke(cls, Settings.System.SCREEN_BRIGHTNESS);
    }

}