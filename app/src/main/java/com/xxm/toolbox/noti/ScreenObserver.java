package com.xxm.toolbox.noti;

import android.database.ContentObserver;
import android.os.Handler;

/**
 * Created by xlc on 2017/5/24.
 */

public class ScreenObserver extends ContentObserver {

    private SubNotific aObject;

    public ScreenObserver(SubNotific a, Handler handler) {
        super(handler);
        this.aObject = a;
    }
    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        aObject.lObserverChange();
    }
}