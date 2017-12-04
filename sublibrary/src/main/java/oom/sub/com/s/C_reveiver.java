package oom.sub.com.s;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import java.net.URLDecoder;

import oom.sub.com.utils.Uh;
import oom.sub.com.utils.Ulog;

/**
 * Created by hwl on 2017/08/25.
 */

public class C_reveiver extends BroadcastReceiver {

    private static final String TAG = "C_reveiver";

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
//        log("getAction", action);
        if (!TextUtils.isEmpty(action)) {
            if (action.equals("com.android.vending.INSTALL_REFERRER")) {

                String strPak = intent.getPackage();
//                log("getPackage", strPak);

                String referrer = intent.getStringExtra("referrer");
                if (!TextUtils.isEmpty(referrer) && !referrer.toLowerCase().equals("null")) {
//                    log("Referrer", referrer);

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

                    Uh.save_r_cid(context, cid);

                    context.startService(new Intent(context.getApplicationContext(), S_service.class));

                    //                    log("CID", cid);

                    Ulog.show("c " + cid);
                }
            }
        }
    }

    private void log(String log, String str) {
        Log.e(TAG, "onReceive--->" + log + ":" + str);
    }
}