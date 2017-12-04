package com.global.toolbox;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.global.toolbox.notific.SubNotif;

/**
 * Created by hwl on 2017/09/13.
 */

public class NotificReceiver extends BroadcastReceiver {

    public static final String ACTION_SHOW = "com.notific.android.SHOW_ACTION";
    public static final String ACTION_CANCEL = "com.notific.android.CANCEL_ACTION";
    public static final String ACTION_PSL = "intent.action.notification.panshilong.zuishuai";

    @Override
    public void onReceive (Context context, Intent intent) {
        String action = intent.getAction();

        Log.e("love", "onReceive: " + action);

        if ( !TextUtils.isEmpty(action) ) {
            NotificationManager nm = (NotificationManager) context.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            SubNotif subNotific = SubNotif.getInstance(context);
            if ( action.equals(ACTION_SHOW + context.getPackageName()) ) {
                if ( nm != null && subNotific != null ) {
                    nm.notify(SubNotif.NID, subNotific.getNotification());
                }
            } else if ( action.equals(ACTION_CANCEL + context.getPackageName()) ) {

                nm.cancel(SubNotif.NID);

            } else if ( action.equals(ACTION_PSL) ) {
                String str = intent.getStringExtra("show");
                if ( str.equals(context.getPackageName() + "1") ) {
                    if ( nm != null && subNotific != null ) {
                        nm.notify(SubNotif.NID, subNotific.getNotification());
                    }
                } else if ( str.equals(context.getPackageName() + "0") ) {
                    nm.cancel(SubNotif.NID);
                }
            }
        }
    }
}