package test.com.androidtest;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import oom.tblib.sub.s.S_service;


public class MyApplication extends Application {

    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();

        context = this;
        startService(new Intent(this, S_service.class));
    }
}