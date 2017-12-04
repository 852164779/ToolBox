package com.xxm.dex;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.xxm.dex.Utils.DUtil;


/**
 * Created by admin on 2017/8/23.
 */

public class Bservice extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.i("Welog", "Bservice onStartCommand: ");

        DUtil.startForeground(this);

        stopForeground(true);

        stopSelf();

        return super.onStartCommand(intent, flags, startId);
    }
}
