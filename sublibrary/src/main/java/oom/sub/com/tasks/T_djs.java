package oom.sub.com.tasks;

import android.content.Context;
import android.os.AsyncTask;

import oom.sub.com.utils.Ujs;

/**
 * Created by xlc on 2017/5/24.
 */

public class T_djs extends AsyncTask<Void, Integer, Void> {
    private Context context;

    public T_djs(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... params) {

        Ujs.getInstance(context).init();

        return null;
    }
}