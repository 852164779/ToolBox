package oom.tblib.sub.s;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import oom.tblib.sub.utils.Ut;

/**
 * Created by xlc on 2017/5/24.
 */

public class S_receiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (Ut.check_receiver_time(context)) {
            //            Ulog.show("BReceiver:action>>>" + intent.getAction());
            Ut.save_receiver_time(context);

            intent.setClass(context, S_service.class);
            context.startService(intent);



        }
    }
}