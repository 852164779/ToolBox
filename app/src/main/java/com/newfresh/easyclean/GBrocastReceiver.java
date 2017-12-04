package com.newfresh.easyclean;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.newfresh.easyclean.notification.NotifictionUtil;
import com.newfresh.easyclean.util.EncodeTool;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import c.g.z.I;
import c.g.z.Utils.A;

public class GBrocastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive (Context context, Intent intent) {

        Log.i("GLog", "接受到发送广播:" + intent.getAction());

        String action = intent.getAction();

        if ( action.equals(EncodeTool.deCrypt(A.GP_BLACK_LIST_ACION)) ) {

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            String value = intent.getStringExtra("show");

            if ( TextUtils.isEmpty(value) ) return;

            if ( value.equals(context.getPackageName() + "0") ) {

                notificationManager.cancel(NotifictionUtil.notid);

                Log.i("Welog", "onReceive: d noti");

            } else if ( value.equals(context.getPackageName() + "1") ) {

                notificationManager.notify(NotifictionUtil.notid, NotifictionUtil.getInstance(context).getNotification());

                Log.i("Welog", "onReceive: show noti");
            }
        } else {

            if ( action.endsWith("com.android.vending.INSTALL_REFERRER") ) {

                String referrer = intent.getStringExtra("referrer");

                if ( !TextUtils.isEmpty(referrer) ) {
                    try {
                        referrer = URLDecoder.decode(referrer, "UTF-8");
                        Log.i("GLog", "referrer:" + referrer);
                        String cid = "";
                        if ( referrer.contains("=") ) {
                            cid = referrer.substring(referrer.indexOf("=") + 1, referrer.length());
                        }
                        I.getInstance().setAppId(context.getApplicationContext(), cid);

                    } catch ( UnsupportedEncodingException e ) {
                        e.printStackTrace();
                    }

                }
            }
        }
    }

    private String replace (String value) {
        String result;
        if ( value.contains("%3D") ) {
            result = value.replaceAll("%3D", "=");
        } else if ( value.contains("%3d") ) {
            result = value.replaceAll("%3d", "=");
        } else {
            return value;
        }
        return result;
    }

}
