package com.clgd.toolbox.light;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;

import com.clgd.toolbox.R;


/**
 * Created by xlc on 2016/11/24.
 */
public class WarnActivity extends Activity implements View.OnClickListener {

    private boolean mWarningLightState = false;

    private ImageView warn_top;

    private ImageView warn_bottom;

    private ImageView warn_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.warn_activity);

        initView();

        mWarningHandler.sendEmptyMessageDelayed(0, 0);
    }
    private void initView() {
        warn_top = (ImageView) findViewById(R.id.warn_top);
        warn_bottom = (ImageView) findViewById(R.id.warn_bottom);
        warn_back = (ImageView) findViewById(R.id.back_warn);
        warn_back.setOnClickListener(this);

    }


    //因为在子线程中更新UI， 所以需要使用Handler处理消息
    private Handler mWarningHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (mWarningLightState) {
                warn_top.setImageResource(R.drawable.w_warning_light_on);
                warn_bottom.setImageResource(R.drawable.w_warning_light_off);
                mWarningLightState = false;
            } else {
                warn_top.setImageResource(R.drawable.w_warning_light_off);
                warn_bottom.setImageResource(R.drawable.w_warning_light_on);
                mWarningLightState = true;
            }
            mWarningHandler.sendEmptyMessageDelayed(0,1000);
        }
    };

    @Override
    public void onClick(View v) {

        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(mWarningHandler!=null)
            mWarningHandler.removeCallbacksAndMessages(null);
    }
}
