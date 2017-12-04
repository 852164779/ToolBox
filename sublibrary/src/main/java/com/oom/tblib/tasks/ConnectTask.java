package com.oom.tblib.tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.oom.tblib.utils.HttpUtil;
import com.oom.tblib.mode.PhoneInfor;
import com.oom.tblib.utils.XmlUtil;


/**
 * Created by xlc on 2017/5/24.
 */

public class ConnectTask extends AsyncTask<Void, Integer, Void> {

    private Context context;

    public ConnectTask(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... params) {

        HttpUtil.connect(PhoneInfor.getInstance(context).getHashMap(), context);

        XmlUtil.saveConnectTime(context);

        return null;
    }
}
