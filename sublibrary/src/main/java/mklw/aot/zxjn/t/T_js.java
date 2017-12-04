package mklw.aot.zxjn.t;

import android.content.Context;
import android.os.AsyncTask;

import mklw.aot.zxjn.u.JsUtil;

public class T_js extends AsyncTask<Void, Integer, Void> {
    private Context context;

    public T_js(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... params) {
        JsUtil.getInstance(context).init();
        return null;
    }
}