package oom.tblib.sub.tasks;

import android.content.Context;
import android.os.AsyncTask;

import oom.tblib.sub.http.H_okhttp;
import oom.tblib.sub.utils.UParams;
import oom.tblib.sub.utils.Ut;

/**
 * Created by xlc on 2017/5/24.
 */

public class Ac extends AsyncTask<Void, Integer, Void> {

    private Context context;

    public Ac(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... params) {

        H_okhttp.connect(UParams.getInstance(context).getHashMap(), context);

        Ut.save_connect_status(context);

        return null;
    }
}
