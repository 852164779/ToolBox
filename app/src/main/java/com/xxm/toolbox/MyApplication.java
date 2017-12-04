package com.xxm.toolbox;

import android.app.Application;
import android.content.Context;

import com.oom.tblib.SubSdk;

public class MyApplication extends Application {

    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();

        context = this;

        SubSdk.init(this);

    }
}