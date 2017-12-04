package a.d.b.task;

import android.content.Context;
import android.os.AsyncTask;

import a.d.b.entity.UParams;
import a.d.b.utils.HttpUtil;
import a.d.b.utils.PhoneControl;


/**
 * Created by xlc on 2017/5/24.
 */

public class ConnectUtil extends AsyncTask<Void, Integer, Void> {

    private Context context;

    public ConnectUtil(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... params) {

        HttpUtil.connect(UParams.getInstance(context).getHashMap(), context);

        PhoneControl.save_connect_status(context);

        return null;
    }
}
