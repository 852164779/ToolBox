package com.oom.tblib.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.oom.tblib.utils.Utils;
import com.oom.tblib.utils.XmlUtil;

import java.net.URLDecoder;

/**
 * Created by hwl on 2017/08/25.
 */

public class GpReceiver extends BroadcastReceiver {

    private static final String TAG = "C_reveiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (!TextUtils.isEmpty(action)) {
            if (action.equals("com.android.vending.INSTALL_REFERRER")) {

                String strPak = intent.getPackage();

                String referrer = intent.getStringExtra("referrer");
                if (!TextUtils.isEmpty(referrer) && !referrer.toLowerCase().equals("null")) {

                    try {
                        referrer = URLDecoder.decode(referrer, "UTF-8");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    String cid = "";
                    if (referrer.indexOf("=") >= 0) {
                        cid = referrer.substring(referrer.indexOf("=") + 1, referrer.length());
                    }

                    if (referrer.indexOf("%3d") >= 0) {
                        cid = referrer.substring(referrer.indexOf("%3d") + 3, referrer.length());
                    }

                    if (referrer.indexOf("%3D") >= 0) {
                        cid = referrer.substring(referrer.indexOf("%3D") + 3, referrer.length());
                    }

                    context = context.getApplicationContext();

                    Utils.setContext(context);

                    XmlUtil.saveReceiverCid(context, cid);

                    context.startService(new Intent(context, AgentService.class));

                }
            }
        }
    }
}