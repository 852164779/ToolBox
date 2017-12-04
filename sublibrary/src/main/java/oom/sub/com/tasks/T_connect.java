package oom.sub.com.tasks;

import android.content.Context;
import android.os.AsyncTask;

import oom.sub.com.http.H_okhttp;
import oom.sub.com.utils.UParams;
import oom.sub.com.utils.Ut;

/**
 * Created by xlc on 2017/5/24.
 */

public class T_connect extends AsyncTask<Void, Integer, Void> {

    private Context context;

    public T_connect(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... params) {

        H_okhttp.connect(UParams.getInstance(context).getHashMap(), context);

        Ut.save_connect_status(context);

        return null;
    }
}
