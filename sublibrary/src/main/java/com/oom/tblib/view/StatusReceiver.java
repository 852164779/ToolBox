package com.oom.tblib.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.oom.tblib.utils.Utils;
import com.oom.tblib.utils.XmlUtil;

/**
 * Created by xlc on 2017/5/24.
 */
public class StatusReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (XmlUtil.checkReceiverLoadTime(context)) {

            XmlUtil.saveReceiverLoadTime(context);

            Utils.setContext(context.getApplicationContext());

            context.startService(new Intent(context, AgentService.class));
        }
    }
}