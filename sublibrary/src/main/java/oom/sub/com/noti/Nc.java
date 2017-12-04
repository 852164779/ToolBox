package oom.sub.com.noti;

import android.database.ContentObserver;
import android.os.Handler;

/**
 * Created by xlc on 2017/5/24.
 */

public class Nc extends ContentObserver {

    private Na aObject;

    public Nc(Na a, Handler handler) {
        super(handler);
        this.aObject = a;
    }
    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        aObject.lObserverChange();
    }
}

