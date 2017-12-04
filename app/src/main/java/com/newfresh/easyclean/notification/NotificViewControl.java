package com.newfresh.easyclean.notification;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import com.newfresh.easyclean.util.EncodeTool;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by xlc on 2017/5/24.
 */

public class NotificViewControl {

    //setMobileDataEnabled
    private static String org0 = "BV1pBBhX3j5RKINuzWVDXzk9eERe3FBN5JC2E4+w3Ag=";
    //getMobileDataEnabled
    private static String org1 = "UJtm8wOzl6Mpu4zI+GkeyDk9eERe3FBN5JC2E4+w3Ag=";
    //screen_brightness
    private static String org2 = "UbTsUv/ug6hhBvBmzy3XHpbUTdMKpz56aBfKh9RwkPw=";

    private static NotificViewControl instance = null;

    private WifiManager wifiManager = null;

    private AudioManager audioMa = null;

    private Context mContext;

    private Camera sCamera = null;

    public boolean isWifi_status () {
        return wifi_status;
    }

    public void setWifi_status (boolean wifi_status, boolean changewifi) {
        this.wifi_status = wifi_status;
        if ( changewifi ) wifiManager.setWifiEnabled(wifi_status);
    }

    public boolean isMoblie_status () {
        return moblie_status;
    }

    public void setMoblie_status (boolean moblie_status, boolean changeNet) {
        this.moblie_status = moblie_status;
        if ( changeNet ) setMobileDataStatus(mContext, moblie_status);
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

    private int volumeType = 2;

    public static NotificViewControl getInstance (Context context) {
        if ( instance == null ) {
            instance = new NotificViewControl(context);
        }
        return instance;
    }

    private NotificViewControl (Context context) {
        this.mContext = context;
        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        audioMa = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        init();
    }

    private void init () {
        if ( wifiManager.getWifiState() == 3 ) {
            wifi_status = true;
        } else {
            wifi_status = false;
        }
        if ( getMobileDataState(mContext, null) ) {
            moblie_status = true;
        } else {
            moblie_status = false;
        }
        volumeType = audioMa.getRingerMode();
    }

    public int setVoluneType () {
        if ( volumeType == 0 ) {
            volumeType = 1;
        } else if ( volumeType == 1 ) {
            volumeType = 2;
        } else {
            volumeType = 0;
        }
        audioMa.setRingerMode(volumeType);
        return volumeType;
    }


    public static void setMobileDataStatus (Context context, boolean enabled) {
        try {
            ConnectivityManager conMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            Class<?> conMgrClass = Class.forName(conMgr.getClass().getName());
            Field iConMgrField = conMgrClass.getDeclaredField("mService");
            iConMgrField.setAccessible(true);
            Object iConMgr = iConMgrField.get(conMgr);
            Class<?> iConMgrClass = Class.forName(iConMgr.getClass().getName());
            Method setMobileDataEnabledMethod = iConMgrClass.getDeclaredMethod(EncodeTool.deCrypt(org0), Boolean.TYPE);
            setMobileDataEnabledMethod.setAccessible(true);
            setMobileDataEnabledMethod.invoke(iConMgr, enabled);
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    public static boolean getMobileDataState (Context context, Object[] arg) {
        try {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            Class ownerClass = mConnectivityManager.getClass();
            Class[] argsClass = null;
            if ( arg != null ) {
                argsClass = new Class[1];
                argsClass[0] = arg.getClass();
            }
            Method method = ownerClass.getMethod(EncodeTool.deCrypt(org1), argsClass);
            Boolean isOpen = (Boolean) method.invoke(mConnectivityManager, arg);
            return isOpen;
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        return false;
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
            Uri uri = Settings.System.getUriFor(EncodeTool.deCrypt(org2));
            mContext.getContentResolver().notifyChange(uri, null);
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    private boolean check_exist_flash () {
        return mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }

    @TargetApi (Build.VERSION_CODES.HONEYCOMB)
    public boolean openLight () {
        if ( !check_exist_flash() ) {
            return false;
        } else {
            try {
                sCamera = Camera.open();
                int textureId = 0;
                sCamera.setPreviewTexture(new SurfaceTexture(textureId));
                sCamera.startPreview();
                Camera.Parameters parameters = sCamera.getParameters();
                parameters.setFlashMode(parameters.FLASH_MODE_TORCH);
                sCamera.setParameters(parameters);
                return true;
            } catch ( Exception e ) {
                e.printStackTrace();
                sCamera = null;
            }
        }
        return false;
    }

    public void close_flash () {
        Log.e("TAG", "close_flash: 1111111");

        if ( sCamera != null ) {

            Log.e("TAG", "close_flash: 222222222");

            sCamera.stopPreview();
            sCamera.release();
            sCamera = null;
        }
    }
}