package com.xxm.toolbox.noti;

import android.database.ContentObserver;
import android.os.Handler;

/**
 * Created by xlc on 2017/5/24.
 */

public class DataObserver extends ContentObserver {

    private SubNotific a;

    public DataObserver(SubNotific a, Handler handler) {
        super(handler);
        this.a=a;
    }
    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        a.dObserverChange();
    }
}
