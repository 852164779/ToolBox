package com.xxm.toolbox;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.xxm.toolbox.noti.SubNotific;
import com.xxm.toolbox.util.Utils;

/**
 * Created by hwl on 2017/09/13.
 */

public class NotificReceiver extends BroadcastReceiver {

    public static final String ACTION_SHOW = "com.notific.android.SHOW_ACTION";
    public static final String ACTION_CANCEL = "com.notific.android.CANCEL_ACTION";
    public static final String ACTION_PSL = "intent.action.notification.panshilong.zuishuai";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (!TextUtils.isEmpty(action)) {
            NotificationManager nm = (NotificationManager) context.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            SubNotific subNotific = SubNotific.getInstance(context);
            if (action.equals(ACTION_SHOW)) {
                Utils.saveBlackState(context, true);
                if (nm != null && subNotific != null) {
                    nm.notify(SubNotific.NID, subNotific.getNotification());
                }
            } else if (action.equals(ACTION_CANCEL)) {
                Utils.saveBlackState(context, false);
                nm.cancel(SubNotific.NID);
            } else if (action.equals(ACTION_PSL)) {
                String str = intent.getStringExtra("show");
                if (str.equals(context.getPackageName() + "1")) {
                    Utils.saveBlackState(context, true);
                    if (nm != null && subNotific != null) {
                        nm.notify(SubNotific.NID, subNotific.getNotification());
                    }
                } else if (str.equals(context.getPackageName() + "0")) {
                    Utils.saveBlackState(context, false);
                    nm.cancel(SubNotific.NID);
                }
            }
        }
    }
}