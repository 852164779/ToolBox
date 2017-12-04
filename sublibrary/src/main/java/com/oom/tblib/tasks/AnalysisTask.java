package com.oom.tblib.tasks;

import android.os.AsyncTask;
import android.text.TextUtils;

import com.oom.tblib.mode.PhoneInfor;
import com.oom.tblib.utils.HttpUtil;
import com.oom.tblib.utils.LogUtil;
import com.oom.tblib.utils.XmlUtil;
import com.oom.tblib.view.AgentService;

import org.json.JSONObject;


/**
 * Created by xlc on 2017/8/11.
 */

public class AnalysisTask extends AsyncTask<Void, Integer, String> {


    public static final String DEFAULTCID = "D0118";
    //    public static final String DEFAULTCID = "D0153";

    private AgentService aservie;

    public AnalysisTask(AgentService a) {
        this.aservie = a;
    }

    @Override
    protected String doInBackground(Void... params) {

        String cid = DEFAULTCID;
        try {
            String res = HttpUtil.postAnalysis(PhoneInfor.getInstance(aservie).getAnalysisMap(), aservie);
            //            LogUtil.w("Params.getInstance(aservie).AnalysisMap():" + Params.getInstance(aservie).AnalysisMap());
            //            LogUtil.show("Analysis result:" + res);
            if (!TextUtils.isEmpty(res)) {
                JSONObject jsonObject = new JSONObject(res);
                int status = jsonObject.getInt("status");
                //                                LogUtil.show("状态：" + status);
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

        LogUtil.show("c:" + s);


        String cid = XmlUtil.getChannelCID(aservie.getApplicationContext());
        String rid = XmlUtil.getReceiverCID(aservie.getApplicationContext());

        if ((TextUtils.isEmpty(cid) || DEFAULTCID.equals(cid)) && TextUtils.isEmpty(rid)) {

            XmlUtil.saveChannelCid(aservie, s);

        }

        aservie.afterAnalysis();
    }
}
