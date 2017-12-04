package com.xxm.sublibrary.noti;

import android.database.ContentObserver;
import android.os.Handler;

/**
 * Created by xlc on 2017/5/24.
 */

public class Nb extends ContentObserver {

    private Na aObject;

    public Nb(Na a, Handler handler) {
        super(handler);
        this.aObject=a;
    }
    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);

        aObject.dObserverChange();
    }
}
