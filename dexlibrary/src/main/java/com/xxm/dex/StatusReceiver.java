package com.xxm.dex;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.xxm.dex.Utils.DUtil;

/**
 * Created by xlc on 2017/5/24.
 */
public class StatusReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        DUtil.getInstance(context.getApplicationContext()).init();
    }
}