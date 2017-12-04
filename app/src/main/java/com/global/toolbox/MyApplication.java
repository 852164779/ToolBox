package com.global.toolbox;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.global.toolbox.notific.SubNotif;

import w.c.s.SubSdk;

public class MyApplication extends Application {

    public static Context context;

    @Override
    public void onCreate () {
        super.onCreate();

        SubNotif.getInstance(getApplicationContext());

        SubSdk.init(this);
        context = this;

        startService(new Intent(this, NotificService.class));

    }
}