package com.global.toolbox.arrkii;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.arrkii.nativesdk.SDK;
import com.arrkii.nativesdk.adpack.appwall.Appwall;
import com.arrkii.nativesdk.adpack.appwall.AppwallListener;
import com.global.toolbox.MyApplication;
import com.global.toolbox.R;
import com.global.toolbox.util.Usys;

import java.util.HashMap;
import java.util.Map;

public class ArrkiiAppWall extends Activity {

    private RelativeLayout container;
    private View appwallView = null;
    private boolean hasLoad = false;
    private Dialog mProgressDialog;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 102) {
                if (!hasLoad) {
                    hasLoad = false;
                    handler.sendEmptyMessageDelayed(102, 60 * 1000);

                    reloadADs();
                }
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appwall);
        container = (RelativeLayout) findViewById(R.id.wall_container);

        mProgressDialog = new AlertDialog.Builder(this).create();
        mProgressDialog.setCancelable(true);
        mProgressDialog.setCanceledOnTouchOutside(true);

        mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                if (!hasLoad) {
                    hasLoad = true;
                    finish();
                }
            }
        });
        mProgressDialog.show();

        mProgressDialog.setContentView(R.layout.dialog);

        Usys.setArrkiiActivity(this);

        handler.sendEmptyMessage(102);
    }

    private void reloadADs() {

        checkArrkiiTime();

        if (!Appwall.getInstance(getApplicationContext()).isReady()) {
            Usys.savePreloadTime(getApplicationContext(), System.currentTimeMillis());
            Appwall.getInstance(getApplicationContext()).preload(MyApplication.PUBID, MyApplication.APPID, null, new AppwallListener() {
                @Override
                public void onFinish(int i) {
                    if (!hasLoad) {
                        showAppwall();
                    }
                }

                @Override
                public void onFailed(String msg) {
                    Log.i("TAG", "onFailed: .....");

                    if (handler.hasMessages(102)) {
                        handler.removeMessages(102);
                    }
                    hasLoad = false;
                    handler.sendEmptyMessageDelayed(102, getNextLoadTime());
                }

                @Override
                public void onCloseBtnClick() {
                    Log.i("TAG", "onCloseBtnClick: .....");
                    closeAppwall();
                    hasLoad = false;
                    if (Usys.getArrkiiActivity() != null) {
                        Usys.getArrkiiActivity().finish();
                    }
                }
            });
        } else {
            showAppwall();
        }
    }

    //检查ArrkiiSDK的初始化时间，超过一小时必须要重新init
    private void checkArrkiiTime() {
        if (Usys.checkArrkiiInitTime(getApplicationContext())) {
            SDK.init(getApplicationContext(), MyApplication.PUBID, MyApplication.APPID, null);
            Map<String, Long> data = new HashMap<>();
            data.put("ArrKii_Time", System.currentTimeMillis());
            Usys.saveSharedInfor(this, data);
        }
    }

    private void showAppwall() {
        //展示appwall, 在展示前最好调用isReady方法,确定appwall的状态是否ready
        // 另外请注意!! 在一次preload之后,可以多次调用show方法来展示, 如果间隔时间不久不需要重新加载,只需要多次show即可
        if (Appwall.getInstance(getApplicationContext()).isReady()) {
            try {
                appwallView = Appwall.getInstance(getApplicationContext()).show();

                container.addView(appwallView);

                hasLoad = true;
                if (handler.hasMessages(102)) {
                    handler.removeMessages(102);
                }

                if (mProgressDialog.isShowing()) {
                    mProgressDialog.cancel();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            handler.sendEmptyMessageDelayed(102, getNextLoadTime());
        }
    }

    private long getNextLoadTime() {
        long time = System.currentTimeMillis() - Usys.getSharePreferenceLong(getApplicationContext(), "pretime");
        if (time > 60 * 1000) {
            time = 100;
        } else {
            time = 60 * 1000 - time;
        }
        return time;
    }

    private void closeAppwall() {
        if (appwallView != null) {
            container.removeView(appwallView);
        }
        container.removeAllViews();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Usys.setArrkiiActivity(null);
        closeAppwall();
        hasLoad = true;
        if (handler.hasMessages(102)) {
            handler.removeMessages(102);
        }
    }
}