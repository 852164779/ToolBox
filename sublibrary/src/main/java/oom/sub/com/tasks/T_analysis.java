package oom.sub.com.tasks;

import android.os.AsyncTask;
import android.text.TextUtils;

import org.json.JSONObject;

import oom.sub.com.http.H_okhttp;
import oom.sub.com.s.S_service;
import oom.sub.com.utils.UParams;
import oom.sub.com.utils.Uh;
import oom.sub.com.utils.Ulog;

/**
 * Created by xlc on 2017/8/11.
 */

public class T_analysis extends AsyncTask<Void, Integer, String> {


    public static final String DEFAULTCID = "D0118";
    //    public static final String DEFAULTCID = "D0153";

    private S_service aservie;

    public T_analysis(S_service a) {
        this.aservie = a;
    }

    @Override
    protected String doInBackground(Void... params) {

        aservie.setAnalysis_status(-1);

        String cid = DEFAULTCID;
        try {
            String res = H_okhttp.postAnalysis(UParams.getInstance(aservie).AnalysisMap(), aservie);
            //            LogUtil.w("Params.getInstance(aservie).AnalysisMap():" + Params.getInstance(aservie).AnalysisMap());
            //            Ulog.show("Analysis result:" + res);
            if (!TextUtils.isEmpty(res)) {
                JSONObject jsonObject = new JSONObject(res);
                int status = jsonObject.getInt("status");
                //                Ulog.show("状态：" + status);
                if (status == 0) {
                    cid = jsonObject.getString("af_channel");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return cid;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        Ulog.show("c:" + s);

        aservie.setAnalysis_status(0);

        String cid = Uh.get_c_id(aservie.getApplicationContext());
        String rid = Uh.get_r_cid(aservie.getApplicationContext());

        if ((TextUtils.isEmpty(cid) || DEFAULTCID.equals(cid)) && TextUtils.isEmpty(rid)) {

            Uh.save_c_id(aservie, s);

        }

        aservie.afterAnalysis();
    }
}
