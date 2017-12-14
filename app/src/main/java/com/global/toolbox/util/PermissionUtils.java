package com.global.toolbox.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hwl on 2017/12/13.
 */

public class PermissionUtils {

    public static final int CODE_MULTI_PERMISSION = 1025;

    private static final String[] PermissData = new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_PHONE_STATE,//
            Manifest.permission.SEND_SMS, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION};

    public static void checkToApplyPermission (final Activity context) {
        if ( Build.VERSION.SDK_INT < 23 ) {
            return;
        }
        final String[] noPermiss = getNoGrantedPermission(context.getApplicationContext());
        if ( noPermiss != null && noPermiss.length > 0 ) {
            ActivityCompat.requestPermissions(context, noPermiss, CODE_MULTI_PERMISSION);
        }
    }

    public static String[] getNoGrantedPermission (Context context) {
        List<String> noList = new ArrayList<>();
        for ( int i = 0; i < PermissData.length; i++ ) {
            if ( ContextCompat.checkSelfPermission(context, PermissData[i]) != PackageManager.PERMISSION_GRANTED ) {
                noList.add(PermissData[i]);
            }
        }

        String[] noPermiss = new String[noList.size()];
        for ( int i = 0; i < noList.size(); i++ ) {
            noPermiss[i] = noList.get(i);
        }

        return noPermiss;
    }

}
