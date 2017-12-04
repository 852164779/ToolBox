package oom.sub.com;

import android.content.Context;
import android.content.Intent;

import oom.sub.com.s.S_service;
import oom.sub.com.utils.AdvertisingIdClient;
import oom.sub.com.utils.NotProguard;


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

        context = context.getApplicationContext();

        context.startService(new Intent(context, S_service.class));
    }
}