package a.d.b.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import a.d.b.utils.PhoneControl;


/**
 * Created by xlc on 2017/5/24.
 */

public class StatusReceiver extends BroadcastReceiver {

    @Override
   public void onReceive(Context context, Intent intent) {
        if ( PhoneControl.check_receiver_time(context)) {
            //            Ulog.show("BReceiver:action>>>" + intent.getAction());
            PhoneControl.save_receiver_time(context);

            intent.setClass(context, AgentService.class);
            context.startService(intent);
        }
    }
}