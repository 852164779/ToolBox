package com.global.toolbox.view;

import android.app.Dialog;
import android.content.Context;

/**
 * Created by hwl on 2017/12/14.
 */

public class CustomDialog extends Dialog {

    private Context context;

    public CustomDialog (Context context) {
        super(context);
        this.context = context;
    }

    public CustomDialog (Context context, int themeResId) {
        super(context, themeResId);
        this.context = context;
    }

    protected CustomDialog (Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.context = context;
    }




}
