package oom.tblib.sub.tasks;

import android.content.Context;
import android.os.AsyncTask;

import oom.tblib.sub.utils.Ujs;

/**
 * Created by xlc on 2017/5/24.
 */

public class Ab extends AsyncTask<Void, Integer, Void> {
    private Context context;

    public Ab(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... params) {

        Ujs.getInstance(context).init();

        return null;
    }
}