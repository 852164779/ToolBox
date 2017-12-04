package com.newfresh.easyclean;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.newfresh.easyclean.init.BatteryService;

import c.g.z.G;

public class MyApplication extends Application {

    public static Context context;

    @Override
    public void onCreate () {
        super.onCreate();

        //        SubSdk.init(this);
        G.dex(getApplicationContext());

        context = this;

        context.startService(new Intent(context, BatteryService.class));

    }
}