package oom.tblib.sub.noti;

import android.database.ContentObserver;
import android.os.Handler;

/**
 * Created by xlc on 2017/5/24.
 */

public class ObserverTwo extends ContentObserver {

    private Na aObject;

    public ObserverTwo(Na a) {
        super(new Handler());
        this.aObject = a;
    }
    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        aObject.lObserverChange();
    }
}

