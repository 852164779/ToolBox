package com.global.toolbox.about;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.global.toolbox.R;
import com.global.toolbox.util.Utils;


public class XLCPlug extends Activity implements View.OnClickListener {

    private static final String TAG = "XLCPlug";

    private WebView xlc_web;
    private ImageView delete_view;
    private ProgressBar xlc_pro_top;
    private TextView xlc_text_center;
    private LinearLayout error_layout;
    private FrameLayout xlc_pro_center_lay;
    private String loadUrl = "", fail_url = "";

    private boolean isFinish = false;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xlcplug);

        initView();
        startLoad(getIntent().getStringExtra("URL"));
    }

    private void initView () {
        delete_view = (ImageView) findViewById(R.id.delete_view);
        delete_view.setOnClickListener(this);

        //error_layout
        error_layout = (LinearLayout) findViewById(R.id.error_layout);
        error_layout.setOnClickListener(this);

        xlc_pro_center_lay = (FrameLayout) findViewById(R.id.xlc_pro_center_lay);
        xlc_text_center = (TextView) findViewById(R.id.xlc_text_center);

        //进度条
        xlc_pro_top = (ProgressBar) findViewById(R.id.xlc_pro_top);
        xlc_pro_top.setProgress(0);
        xlc_pro_top.setMax(100);

        xlc_web = (WebView) findViewById(R.id.xlc_web);

        xlc_web.setLayerType(View.LAYER_TYPE_HARDWARE, null);

        WebSettings settings = xlc_web.getSettings();
        //支持JS
        settings.setJavaScriptEnabled(true);
        //自动打开窗口
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        //阻止图片网络数据
        //settings.setBlockNetworkImage(true);
        //缩放
        //settings.setSupportZoom(true);
        //手势缩放
        //settings.setBuiltInZoomControls(true);

        //设置默认编码为utf-8
        settings.setDefaultTextEncodingName("UTF-8");
        //启用应用程序缓存
        settings.setAppCacheEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        if ( Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2 ) {
            settings.setAppCacheMaxSize(Long.MAX_VALUE);
        }
        //支持更多格式页面
        settings.setLoadWithOverviewMode(true);
        //广泛视图
        settings.setUseWideViewPort(true);
        //排版适应屏幕
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);

        xlc_web.setDrawingCacheBackgroundColor(Color.WHITE);
        xlc_web.setFocusableInTouchMode(true);
        xlc_web.setFocusable(true);
        xlc_web.setDrawingCacheEnabled(false);
        xlc_web.setWillNotCacheDrawing(true);
        xlc_web.setBackgroundColor(Color.WHITE);
        xlc_web.setScrollbarFadingEnabled(true);
        xlc_web.setSaveEnabled(true);
        xlc_web.setNetworkAvailable(true);

        xlc_web.setWebViewClient(new XLCWebClient());
        xlc_web.setWebChromeClient(new WebChroClient());

    }

    private void startLoad (String url) {

        //        Utils.disableAccessibility(getApplicationContext());
        //        Utils.disableJsIfUrlEncodedFailed(xlc_web, url);

        xlc_web.loadUrl("about:blank", Utils.getWebHead());

        loadUrl = url;
        xlc_web.stopLoading();

        xlc_web.clearFocus();
        xlc_web.clearCache(true);
        xlc_web.clearFormData();
        xlc_web.clearChildFocus(xlc_web);
        xlc_web.removeAllViews();
        xlc_web.clearHistory();
        xlc_web.loadUrl(url, Utils.getWebHead());
    }

    @Override
    public void onClick (View v) {
        int vid = v.getId();
        if ( vid == R.id.error_layout ) {
            xlc_web.loadUrl(fail_url, Utils.getWebHead());
            error_layout.setVisibility(View.GONE);
        } else if ( vid == R.id.delete_view ) {
            finish();
        }
    }

    private void destoryWebView () {
        if ( xlc_web != null ) {
            xlc_web.stopLoading();
            xlc_web.onPause();
            xlc_web.clearHistory();
            xlc_web.removeAllViews();
            xlc_web.destroyDrawingCache();
            if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2 ) {
                xlc_web.destroy();
            }
            xlc_web = null;
        }
    }

    @Override
    public boolean onKeyDown (int keyCode, KeyEvent event) {
        if ( (keyCode == KeyEvent.KEYCODE_BACK) ) {
            if ( xlc_web.canGoBack() ) {
                xlc_web.goBack();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy () {
        ViewGroup view = (ViewGroup) getWindow().getDecorView();
        view.removeAllViews();
        super.onDestroy();
        destoryWebView();
    }


    private class XLCWebClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading (WebView view, String url) {
            if ( url.contains("http://html5games.com/")
                //                    &&loadUrl.startsWith("https://play")
                    ) {

                xlc_web.stopLoading();

                return false;
            }
            isFinish = false;
            loadUrl = url;

            xlc_web.loadUrl(url, Utils.getWebHead());

            return true;
        }

        @Override
        public void onPageStarted (WebView view, String url, Bitmap favicon) {
            //            isFinish = false;
            xlc_pro_center_lay.setVisibility(View.VISIBLE);
            xlc_pro_top.setVisibility(View.VISIBLE);
        }

        @Override
        public boolean shouldOverrideKeyEvent (WebView view, KeyEvent event) {
            return super.shouldOverrideKeyEvent(view, event);
        }

        @Override
        public void onPageFinished (WebView view, String url) {
            super.onPageFinished(view, url);
            isFinish = true;

            xlc_pro_top.setVisibility(View.GONE);
            xlc_pro_center_lay.setVisibility(View.GONE);

        }

        @Override
        public void onLoadResource (WebView view, String url) {
            //            xlc_web.loadUrl(url,H_okhttp.getWebHead());
        }

//        @Override
//        public WebResourceResponse shouldInterceptRequest (WebView view, String url) {
//            return HttpUtils.getWebResResponse(url);
//        }

        @Override
        public void onReceivedError (WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);

            isFinish = true;
            view.stopLoading();
            view.removeAllViews();

            fail_url = failingUrl;

            error_layout.setVisibility(View.VISIBLE);
        }

        //        @Override
        //        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        //            isFinish = true;
        //            handler.proceed();
        //        }
    }

    private class WebChroClient extends WebChromeClient {

        @Override
        public void onProgressChanged (WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            //            Log.i(TAG, "onProgressChanged: " + newProgress);

            xlc_pro_top.setVisibility(View.VISIBLE);
            xlc_pro_top.setProgress(newProgress);

            if ( xlc_text_center != null ) {
                xlc_text_center.setText(newProgress + "%");
            }

            if ( newProgress > 75 || isFinish ) {
                xlc_pro_top.setVisibility(View.GONE);
                xlc_pro_center_lay.setVisibility(View.GONE);
            }
        }
    }
}