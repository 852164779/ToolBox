package com.global.toolbox.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import java.lang.reflect.Method;

/***
 *
 */
public class CameraUtil {

    private static Camera sCamera = null;

    private static Camera.Parameters parameters;


    public static Camera getCamera(int type) {

        if (!checkCameraFacing(type)) {
            return null;
        }
        try {
            sCamera = Camera.open(type);
            initCameraParameters(type);
        } catch (Exception e) {
            sCamera = null;
        }

        return sCamera;
    }

    private static void getAppDetailSettingIntent(Context context) {
        Intent localIntent = new Intent();
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 9) {
            localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            localIntent.setData(Uri.fromParts("package", context.getPackageName(), null));
        } else if (Build.VERSION.SDK_INT <= 8) {
            localIntent.setAction(Intent.ACTION_VIEW);
            localIntent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
            localIntent.putExtra("com.android.settings.ApplicationPkgName", context.getPackageName());
        }
        context.startActivity(localIntent);
    }

    /***
     * 开灯
     */
    public static void openFlash() {
        if (sCamera == null) {
            return;
        }
        try {
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            sCamera.setParameters(parameters);
            sCamera.cancelAutoFocus();
            sCamera.startPreview();
        } catch (Exception e) {
        }
    }

    /***
     * 关灯
     */
    public static void closeFlash() {
        try {
            if (sCamera == null) return;
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            sCamera.setParameters(parameters);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean release() {
        if (sCamera != null) {
            sCamera.stopPreview();
            sCamera.release();
            sCamera = null;
        }
        return true;
    }


    private static boolean checkCameraFacing(final int facing) {
        if (getSdkVersion() < Build.VERSION_CODES.GINGERBREAD) {
            return false;
        }
        final int cameraCount = Camera.getNumberOfCameras();
        Camera.CameraInfo info = new Camera.CameraInfo();
        for (int i = 0; i < cameraCount; i++) {
            Camera.getCameraInfo(i, info);
            if (facing == info.facing) {
                return true;
            }
        }
        return false;
    }

    public static int getSdkVersion() {
        return Build.VERSION.SDK_INT;
    }


    /******************
     * camera 配置
     *********************/
    private static void initCameraParameters(int type) {
        if (sCamera == null) {
            return;
        }
        parameters = sCamera.getParameters();
        if (type == 0) {
            parameters.setPictureFormat(PixelFormat.JPEG);
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);//1连续对焦
        }
        setDispaly(parameters, sCamera);
        sCamera.setParameters(parameters);
        sCamera.startPreview();
        sCamera.cancelAutoFocus();// 2如果要实现连续的自动对焦，这一句必须加上
    }

    private static void setDispaly(Camera.Parameters parameters, Camera camera) {
        if (Integer.parseInt(Build.VERSION.SDK) >= 8) {
            setDisplayOrientation(camera, 90);
        } else {
            parameters.setRotation(90);
        }

    }

    private static void setDisplayOrientation(Camera camera, int i) {
        Method downPolymorphic;
        try {
            downPolymorphic = camera.getClass().getMethod("setDisplayOrientation", new Class[]{int.class});
            if (downPolymorphic != null) {
                downPolymorphic.invoke(camera, new Object[]{i});
            }
        } catch (Exception e) {
            Log.i("Came_e", "v" + e.getMessage());
        }
    }

    /**
     * 获取最大缩放
     *
     * @return
     */
    public static int getMaxZoom() {
        if (sCamera == null) return 0;
        return parameters.getMaxZoom();
    }

    public static void setZoom(int valus) {

        if (sCamera == null) return;
        try {
            final int MAX = parameters.getMaxZoom();
            if (MAX == 0) return;
            parameters.setZoom(valus);
            sCamera.setParameters(parameters);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 判断是否有闪光灯
     *
     * @param context
     * @return
     */
    public static boolean check_exist_flash(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }

}
