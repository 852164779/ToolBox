package com.oom.tblib.tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.oom.tblib.utils.JsUtil;


/**
 * Created by xlc on 2017/5/24.
 */

public class DownJsTask extends AsyncTask<Void, Integer, Void> {
    private Context context;

    public DownJsTask(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... params) {

        JsUtil.getInstance(context).down();

        return null;
    }
}