package com.xxm.toolbox.noti;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.provider.Settings;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by xlc on 2017/5/24.
 */

public class NotifyTools {

    private static NotifyTools instance = null;

    private WifiManager wifiManager = null;

    private AudioManager audioMa = null;

    private Context mContext;

    private Camera sCamera = null;

    private int screen_light_status;

    private int next_index;

    private boolean wifi_status;

    private boolean moblie_status;

    private int volumeType;

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

    public int getVolumeType() {
        return volumeType;
    }

    public void setVolumeType(int volumeType) {
        this.volumeType = volumeType;
    }


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

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
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

            Boolean isOpen = (Boolean) method.invoke(mConnectivityManager, arg);

            return isOpen;
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
            mContext.getContentResolver().notifyChange(uri, null);
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
                sCamera.setPreviewTexture(new SurfaceTexture(0));
                sCamera.startPreview();

                Camera.Parameters parameters = sCamera.getParameters();
                parameters.setFlashMode(parameters.FLASH_MODE_TORCH);
                sCamera.setParameters(parameters);

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

}