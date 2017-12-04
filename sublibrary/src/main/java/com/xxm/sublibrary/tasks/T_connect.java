package com.xxm.sublibrary.tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.xxm.sublibrary.http.H_okhttp;
import com.xxm.sublibrary.utils.UParams;
import com.xxm.sublibrary.utils.Uutil;

/**
 * Created by xlc on 2017/5/24.
 */

public class T_connect extends AsyncTask<Void,Integer,Void> {

    private Context context;

    public T_connect(Context context)
    {
        this.context=context;
    }
    @Override
    protected Void doInBackground(Void... params) {



        H_okhttp.connect(UParams.getInstance(context).getHashMap(),context);

        Uutil.save_connect_status(context);

        return null;
    }
}
