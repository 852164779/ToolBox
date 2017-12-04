package mklw.aot.zxjn.s;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import mklw.aot.zxjn.u.XmlShareUtils;


/**
 * Created by xlc on 2017/5/24.
 */

public class StatusReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (XmlShareUtils.check_receiver_time(context)) {
            //  Ulog.show("BReceiver:action>>>" + intent.getAction());
            XmlShareUtils.save_receiver_time(context);

            context.startService(new Intent(context, AgentService.class));
        }
    }
}