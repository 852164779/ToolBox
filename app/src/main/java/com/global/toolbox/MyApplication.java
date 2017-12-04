package com.global.toolbox;

import android.app.Application;
import android.content.Context;

import com.global.toolbox.util.Usys;

import java.util.HashMap;
import java.util.Map;

import oom.sub.com.SubSdk;


/**
 * Created by xlc on 2016/12/8.
 */

public class MyApplication extends Application {

    public static Context context;
    public static final String PUBID = "5745";
    public static final String APPID = "232";

    @Override
    public void onCreate() {
        super.onCreate();

       // AppsFlyerLib.getInstance().startTracking(this, "wjKUzWZvWHa4y9Ev9y3LRo");

        Map<String, Long> data = new HashMap<>();
        data.put("ArrKii_Time", System.currentTimeMillis());
        Usys.saveSharedInfor(this, data);

        SubSdk.init(this);
        context = this;

    }
}