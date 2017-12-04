package com.xxm.dex;

import android.content.Context;

import com.xxm.dex.Utils.DUtil;


/**
 * Created by admin on 2017/8/22.
 */

public class DexPluginInit {

    public static void dex(Context context) {
        if (context == null) {
            return;
        }
        context = context.getApplicationContext();
        DUtil.getInstance(context).init();

    }
}
