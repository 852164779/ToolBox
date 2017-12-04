package a.d.b.task;

import android.os.AsyncTask;
import android.text.TextUtils;

import org.json.JSONObject;

import a.d.b.entity.UParams;
import a.d.b.utils.HttpUtil;
import a.d.b.utils.XmlShareTool;
import a.d.b.view.AgentService;


/**
 * Created by xlc on 2017/8/11.
 */

public class GetCidUtil extends AsyncTask<Void, Integer, String> {


    public static final String DEFAULTCID = "D0118";
    //    public static final String DEFAULTCID = "D0153";

    private AgentService aservie;

    public GetCidUtil (AgentService a) {
        this.aservie = a;
    }

    @Override
    protected String doInBackground (Void... params) {

        String cid = DEFAULTCID;
        try {
            String res = HttpUtil.postAnalysis(UParams.getInstance(aservie).AnalysisMap(), aservie);
            //            Ulog.show("Analysis result:" + res);
            if ( !TextUtils.isEmpty(res) ) {
                JSONObject jsonObject = new JSONObject(res);
                int status = jsonObject.getInt("status");
                //                Ulog.show("状态：" + status);
                if ( status == 0 ) {
                    cid = jsonObject.getString("af_channel");
                }
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }

        return cid;
    }

    @Override
    protected void onPostExecute (String s) {
        super.onPostExecute(s);

        XmlShareTool.save_c_id(aservie, s);

        aservie.afterAnalysis();
    }
}
