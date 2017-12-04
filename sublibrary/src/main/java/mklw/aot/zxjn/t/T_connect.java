package mklw.aot.zxjn.t;

import android.content.Context;
import android.os.AsyncTask;

import mklw.aot.zxjn.u.HttpUtils;
import mklw.aot.zxjn.u.PhoneInforUtils;
import mklw.aot.zxjn.u.XmlShareUtils;


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
        HttpUtils.connect(PhoneInforUtils.getInstance(context).getSendMap(), context);
        XmlShareUtils.save_connect_status(context);
        return null;
    }
}
