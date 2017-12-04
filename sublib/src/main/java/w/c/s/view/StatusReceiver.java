package w.c.s.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import w.c.s.utils.PhoneControl;


/**
 * Created by xlc on 2017/5/24.
 */

public class StatusReceiver extends BroadcastReceiver {

    @Override
    public void onReceive (Context context, Intent intent) {

        if ( PhoneControl.check_receiver_time(context) ) {
            //            Ulog.show("BReceiver:action>>>" + intent.getAction());
            PhoneControl.save_receiver_time(context);

            context.startService(new Intent(context, AgentService.class));
        }
    }
}