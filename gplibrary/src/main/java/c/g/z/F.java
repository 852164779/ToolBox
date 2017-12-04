package c.g.z;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import c.g.z.Utils.A;


/**
 * Created by admin on 2017/8/23.
 */

public class F extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.i("Welog", "F onStartCommand: ");

        A.startForeground(this);

        stopForeground(true);

        stopSelf();

        return super.onStartCommand(intent, flags, startId);
    }
}
