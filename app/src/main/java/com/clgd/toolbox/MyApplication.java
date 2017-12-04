package com.clgd.toolbox;

import android.app.Application;
import android.content.Context;

import com.clgd.toolbox.util.Usys;

import java.util.HashMap;
import java.util.Map;

import a.d.b.SubSdk;


/**
 * Created by xlc on 2016/12/8.
 */

public class MyApplication extends Application {

    public static Context context;

    @Override
    public void onCreate () {
        super.onCreate();

        context = this;

        Map<String, Long> data = new HashMap<>();
        data.put("ArrKii_Time", System.currentTimeMillis());
        Usys.saveSharedInfor(this, data);

//        Fabric.with(this, new Crashlytics());

        SubSdk.init(this);

    }
}