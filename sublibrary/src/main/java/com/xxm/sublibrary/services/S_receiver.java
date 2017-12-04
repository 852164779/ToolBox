package com.xxm.sublibrary.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.xxm.sublibrary.utils.Ulog;
import com.xxm.sublibrary.utils.Uutil;

/**
 * Created by xlc on 2017/5/24.
 */

public class S_receiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

        if (Uutil.check_receiver_time(context)) {

            Uutil.save_receiver_time(context);

            Ulog.show("BReceiver:action>>>" + intent.getAction());

            intent.setClass(context, S_service.class);

            context.startService(intent);
        }

    }
}
