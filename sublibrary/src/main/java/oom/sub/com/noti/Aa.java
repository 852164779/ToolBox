package oom.sub.com.noti;

import android.database.ContentObserver;
import android.os.Handler;

/**
 * Created by xlc on 2017/5/24.
 */

public class Aa extends ContentObserver {

    private Na a;

    public Aa(Na a, Handler handler) {
        super(handler);
        this.a=a;
    }
    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        a.dObserverChange();
    }
}
