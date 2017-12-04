package com.xxm.toolbox;

import android.app.Application;
import android.content.Context;

import com.xxm.dex.DexPluginInit;

public class MyApplication extends Application {

    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();

        context = this;

        //        SubSdk.init(this);

        DexPluginInit.dex(getApplicationContext());

    }
}