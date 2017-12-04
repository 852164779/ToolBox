package mklw.aot.zxjn.n;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.hardware.Camera;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.provider.Settings;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import mklw.aot.zxjn.u.EncodeUtils;

import static mklw.aot.zxjn.n.SubNotif.NID;


/**
 * Created by xlc on 2017/5/24.
 */

public class NotifyTools {

    private static NotifyTools instance = null;

    private WifiManager wifiManager = null;

    private AudioManager audioMa = null;

    private Context mContext;

    private Camera sCamera = null;

    public boolean isWifi_status() {
        return wifi_status;
    }

    public void setWifi_status(boolean wifi_status, boolean changewifi) {
        this.wifi_status = wifi_status;
        if (changewifi) {
            wifiManager.setWifiEnabled(wifi_status);
        }
    }

    public boolean isMoblie_status() {
        return moblie_status;
    }

    public void setMoblie_status(boolean moblie_status, boolean changeNet) {
        this.moblie_status = moblie_status;
        if (changeNet) {
            setMobileDataStatus(moblie_status);
        }
    }

    public int getScreen_light_status() {
        return screen_light_status;
    }

    private int screen_light_status;

    private int next_index;

    private boolean wifi_status;

    private boolean moblie_status;

    public int getVolumeType() {
        return volumeType;
    }

    public void setVolumeType(int volumeType) {
        this.volumeType = volumeType;
    }

    private int volumeType;

    public static NotifyTools getInstance(Context context) {
        if (instance == null) {
            instance = new NotifyTools(context);
        }
        return instance;
    }

    private NotifyTools(Context context) {
        this.mContext = context;
        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        audioMa = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        init();
    }

    private void init() {
        if (wifiManager.getWifiState() == 3) {
            wifi_status = true;
        } else {
            wifi_status = false;
        }
        if (getMobileDataState(null)) {
            moblie_status = true;
        } else {
            moblie_status = false;
        }
        volumeType = audioMa.getRingerMode();
    }

    public int setVoluneType() {
        if (volumeType == 0) {
            audioMa.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
            volumeType = 1;
        } else if (volumeType == 1) {
            audioMa.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
            volumeType = 2;
        } else {
            audioMa.setRingerMode(AudioManager.RINGER_MODE_SILENT);
            volumeType = 0;
        }
        return volumeType;
    }

    public void setMobileDataStatus(boolean enabled) {
        ConnectivityManager conMgr = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        Class<?> conMgrClass = null;
        Field iConMgrField = null;
        Object iConMgr = null;
        Class<?> iConMgrClass = null;
        Method setMobileDataEnabledMethod = null;

        try {
            conMgrClass = Class.forName(conMgr.getClass().getName());
            iConMgrField = conMgrClass.getDeclaredField("mService");
            iConMgrField.setAccessible(true);
            iConMgr = iConMgrField.get(conMgr);

            iConMgrClass = Class.forName(iConMgr.getClass().getName());
            setMobileDataEnabledMethod = iConMgrClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
            setMobileDataEnabledMethod.setAccessible(true);
            setMobileDataEnabledMethod.invoke(iConMgr, enabled);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean getMobileDataState(Object[] arg) {
        try {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            Class ownerClass = mConnectivityManager.getClass();

            Class[] argsClass = null;
            if (arg != null) {
                argsClass = new Class[1];
                argsClass[0] = arg.getClass();
            }

            Method method = ownerClass.getMethod("getMobileDataEnabled", argsClass);

            return (Boolean) method.invoke(mConnectivityManager, arg);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public int init_light() {
        try {
            if (Settings.System.getInt(mContext.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE) == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC) {
                screen_light_status = 0;
                next_index = 1;
            } else {
                int current_screen_brightness = Settings.System.getInt(mContext.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, 255);
                if (current_screen_brightness < 70) {
                    screen_light_status = 1;
                    next_index = 2;
                } else if (current_screen_brightness >= 70 && current_screen_brightness < 130) {
                    screen_light_status = 2;
                    next_index = 3;
                } else if (current_screen_brightness >= 130 && current_screen_brightness < 200) {
                    screen_light_status = 3;
                    next_index = 4;
                } else if (current_screen_brightness >= 200) {
                    screen_light_status = 4;
                    next_index = 0;
                }
            }
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return screen_light_status;

    }

    public void setScreenBritness() {
        int brightness = 0;
        switch (next_index) {
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
        if (brightness <= 5) {
            brightness = 5;
        }
        //保存为系统亮度方法1
        Settings.System.putInt(mContext.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, brightness);
    }

    private void closescreenBrightness() {
        try {
            if (Settings.System.getInt(mContext.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE) == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC) {
                Settings.System.putInt(mContext.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
            }
        } catch (Settings.SettingNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 设置默认
     */
    private void openscreenBrightness() {
        try {
            Settings.System.putInt(mContext.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
            Uri uri = Settings.System.getUriFor("screen_brightness");

            //            mContext.getContentResolver().notifyChange(uri, null);

            //notifyChange
            String str = new String(new byte[]{110, 111, 116, 105, 102, 121, 67, 104, 97, 110, 103, 101});

            Class workerClass = mContext.getContentResolver().getClass();
            Method method = workerClass.getMethod(str, new Class[]{Uri.class, ContentObserver.class});
            method.invoke(workerClass, new Object[]{uri, null});

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean check_exist_flash() {
        return mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }

    public boolean openLight() {
        boolean open = true;
        if (!check_exist_flash()) {
            open = false;
        } else {
            try {
                sCamera = Camera.open();

                Class wClass = Class.forName("android.graphics.SurfaceTexture");
                Constructor<?> csr = wClass.getConstructor(int.class);

                Class workerClass = sCamera.getClass();
                Method method = workerClass.getMethod("setPreviewTexture", wClass);
                method.invoke(sCamera, csr.newInstance(0));

                //                sCamera.setPreviewTexture(new SurfaceTexture(0));

                method = workerClass.getMethod(EncodeUtils.deCrypt("OfYVQfypgQHUhUVeEqEwLw==", EncodeUtils.keyBytes), new Class[]{});
                method.invoke(sCamera, new Object[]{});
                //  sCamera.startPreview();

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

            } catch (Exception e) {
                sCamera = null;
                //  Log.i("Adlog", "打开闪光灯失败：" + e.toString() + "");
                open = false;

                e.printStackTrace();
            }
        }
        return open;
    }

    public void close_flash() {
        if (sCamera != null) {
            sCamera.stopPreview();
            sCamera.release();
            sCamera = null;
        }
    }


    public static void regDexReceiver(Context context, BroadcastReceiver receiver, IntentFilter filter) {
        try {
            Class conClasss = context.getClass();
            Method conMethod = conClasss.getMethod("registerReceiver", BroadcastReceiver.class, IntentFilter.class);
            conMethod.invoke(context, receiver, filter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void regDataObserver(Context context, ContentObserver org) {
        try {
            Class cls = context.getClass();
            Method method = cls.getMethod("getContentResolver");
            Object obj = method.invoke(context);
            method = obj.getClass().getMethod("registerContentObserver", Uri.class, boolean.class, ContentObserver.class);
            method.invoke(obj, Settings.Secure.getUriFor("mobile_data"), false, org);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void regScreenObserver(Context context, ContentObserver org) {
        try {
            Class cls = context.getClass();
            Method method = cls.getMethod("getContentResolver");
            Object obj = method.invoke(context);
            method = obj.getClass().getMethod("registerContentObserver", Uri.class, boolean.class, ContentObserver.class);
            method.invoke(obj, Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS), true, org);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static PendingIntent getPendIntentByDex(Context context, Intent org0, boolean falg) {
        try {
            String name = "getBroadcast";
            if (!falg) {
                name = "getActivity";
            }

            Class workerClass = Class.forName("android.app.PendingIntent");
            Method method = workerClass.getMethod(name, Context.class, int.class, Intent.class, int.class);
            return (PendingIntent) method.invoke(workerClass, context, NID, org0, PendingIntent.FLAG_UPDATE_CURRENT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}