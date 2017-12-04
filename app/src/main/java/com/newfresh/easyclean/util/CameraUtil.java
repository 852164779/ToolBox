package com.newfresh.easyclean.util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Build;

import static com.newfresh.easyclean.util.OtherUtil.getSdkVersion;

/***
 *
 */
public class CameraUtil {

    private static Camera sCamera = null;
    private static Camera.Parameters parameters;

    public static Camera getCamera (int type) {
        if ( !checkCameraFacing(type) ) {
            return null;
        }

        try {
            sCamera = Camera.open(type);
            initCameraParameters(type);
        } catch ( Exception e ) {
            e.printStackTrace();
            sCamera = null;
        }
        return sCamera;
    }


    /**
     * 开关相机
     *
     * @param org true:开、false:关
     */
    public static void control (boolean org) {
        if ( sCamera == null ) return;

        try {
            if ( org ) {
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                sCamera.setParameters(parameters);
                sCamera.cancelAutoFocus();
                sCamera.startPreview();
            } else {
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                sCamera.setParameters(parameters);
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    public static boolean release () {
        if ( sCamera != null ) {
            sCamera.stopPreview();
            sCamera.release();
            sCamera = null;
        }
        return true;
    }

    private static boolean checkCameraFacing (final int facing) {
        if ( getSdkVersion() < Build.VERSION_CODES.GINGERBREAD ) {
            return false;
        }
        final int cameraCount = Camera.getNumberOfCameras();
        Camera.CameraInfo info = new Camera.CameraInfo();
        for ( int i = 0; i < cameraCount; i++ ) {
            Camera.getCameraInfo(i, info);
            if ( facing == info.facing ) {
                return true;
            }
        }
        return false;
    }

    private static void initCameraParameters (int type) {
        if ( sCamera == null ) return;

        parameters = sCamera.getParameters();
        if ( type == 0 ) {
            parameters.setPictureFormat(PixelFormat.JPEG);
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);//1.连续对焦
        }
        setDispaly(parameters, sCamera);
        sCamera.setParameters(parameters);
        sCamera.startPreview();
        sCamera.cancelAutoFocus();// 2.如果要实现连续的自动对焦，这一句必须加上
    }

    private static void setDispaly (Camera.Parameters parameters, Camera camera) {
        if ( Build.VERSION.SDK_INT >= 8 ) {
            camera.setDisplayOrientation(90);
        } else {
            parameters.setRotation(90);
        }
    }

    /**
     * 获取最大缩放
     *
     * @return
     */
    public static int getMaxZoom () {
        if ( sCamera == null ) return 0;
        return parameters.getMaxZoom();
    }

    public static void setZoom (int valus) {
        if ( sCamera == null ) return;
        try {
            if ( getMaxZoom() == 0 ) return;
            parameters.setZoom(valus);
            sCamera.setParameters(parameters);
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    /**
     * 判断是否有闪光灯
     */
    public static boolean check_exist_flash (Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }

}
