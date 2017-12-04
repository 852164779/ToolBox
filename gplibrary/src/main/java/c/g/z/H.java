package c.g.z;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import c.g.z.Utils.A;


/**
 * Created by admin on 2017/8/18.
 */

public class H extends Service {
    @Override
    public void onCreate() {
        super.onCreate();
        A.startForeground(this);
        startService(new Intent(this, F.class));
        Log.d("Welog", "H onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("Welog", "H onStartCommand");
        try {
            I.getInstance().a(intent,flags,startId);
        } catch (Exception e) {
            Log.i("Welog", "H onStartCommand: 没有加载apk");
            I.getInstance().init(getApplicationContext());
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
        I.getInstance().onDestroy();
        Log.d("Welog", "H onDestroy");
    }
}