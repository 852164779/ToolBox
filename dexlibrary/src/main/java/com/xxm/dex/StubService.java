package com.xxm.dex;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.xxm.dex.Utils.DUtil;


/**
 * Created by admin on 2017/8/18.
 */

public class StubService extends Service {
    @Override
    public void onCreate() {
        super.onCreate();
        DUtil.startForeground(this);
        startService(new Intent(this, Bservice.class));
        Log.d("Welog", "StubService onCreate");
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("Welog", "StubService onStartCommand");
        try {
            ServiceManager.getInstance().onStartCommand(intent, flags, startId);
        } catch (Exception e) {
            Log.i("StubService", "StubService onStartCommand: 没有加载apk");
            ServiceManager.getInstance().setup(getApplicationContext());
        }
        return super.onStartCommand(intent, flags, startId);
    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ServiceManager.getInstance().onDestroy();
        Log.d("Welog", "StubService onDestroy");
    }
}