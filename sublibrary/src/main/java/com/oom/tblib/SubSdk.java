package com.oom.tblib;

import android.content.Context;
import android.content.Intent;

import com.oom.tblib.utils.AdvertisingIdClient;
import com.oom.tblib.utils.NotProguard;
import com.oom.tblib.utils.Utils;
import com.oom.tblib.view.AgentService;


/**
 * Created by xlc on 2017/5/17.
 */
@NotProguard
public class SubSdk {

    public static void init(Context context) {
        if (null == context) {
            return;
        }

        AdvertisingIdClient.getAdvertisingId(context);

        Utils.setContext(context);

        context.startService(new Intent(context, AgentService.class));



    }
}