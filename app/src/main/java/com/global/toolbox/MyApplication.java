package com.global.toolbox;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.arrkii.nativesdk.SDK;
import com.global.toolbox.util.Usys;
import com.xxm.sublibrary.services.S_service;

import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class MyApplication extends Application {

    public static Context context;
    public static final String PUBID = "5745";
    public static final String APPID = "232";

    @Override
    public void onCreate() {
        super.onCreate();
        //    Fabric.with(this, new Crashlytics());
        context = this;


        SDK.init(this, MyApplication.PUBID, MyApplication.APPID, null);
        //        SDK.getInstance(this).setGAID("1234567890123");
        Map<String, Long> data = new HashMap<>();
        data.put("ArrKii_Time", System.currentTimeMillis());
        Usys.saveSharedInfor(this, data);


        Log.d(TAG, "onCreate: " + this.getFilesDir().getPath());

        startService(new Intent(this, S_service.class));
    }
}