package a.d.b.notif;

import android.database.ContentObserver;
import android.os.Handler;

/**
 * Created by xlc on 2017/5/24.
 */

public class Aa extends ContentObserver {

    private SubNotif a;

    public Aa(SubNotif a, Handler handler) {
        super(handler);
        this.a=a;
    }
    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        a.dObserverChange();
    }
}
