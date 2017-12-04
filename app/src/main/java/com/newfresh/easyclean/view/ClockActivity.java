package com.newfresh.easyclean.view;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.newfresh.easyclean.R;
import com.newfresh.easyclean.adapter.ClockAdapter;
import com.newfresh.easyclean.bean.Clock;
import com.newfresh.easyclean.custom.TimeView;
import com.newfresh.easyclean.util.ClockUtil;

import java.util.List;

/**
 * Created by xlc on 2016/11/29.
 */
public class ClockActivity extends Activity implements View.OnClickListener {

    private TextView start_btn, check_btn;

    private TimeView mTimeView;

    private ClockUtil timer;

    private MyHandler mHandler;

    private ClockAdapter adapter;

    private ListView listView;

    private List<Clock> list;

    private MediaPlayer mp;//mediaPlayer对象

    public boolean isRunning () {
        return timer.isRunning();
    }

    public void updateTime () {
        timer.update();
        ClockUtil.TwoTuple<Long, Long> result = timer.getNowTime();
        mTimeView.setTime(result._2);
    }


    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.clock_activity);

        timer = new ClockUtil(getApplicationContext());

        list = timer.getList();

        mTimeView = (TimeView) findViewById(R.id.time_view);
        start_btn = (TextView) findViewById(R.id.start);
        check_btn = (TextView) findViewById(R.id.check_times);
        start_btn.setOnClickListener(this);
        check_btn.setOnClickListener(this);

        listView = (ListView) findViewById(R.id.list_view);

        adapter = new ClockAdapter(getApplicationContext(), list);

        listView.setAdapter(adapter);

        mHandler = new MyHandler(ClockActivity.this);
    }

    private void release () {
        if ( mp != null ) {
            mp.release();
            mp = null;
        }
    }

    @Override
    public void onClick (View v) {
        switch ( v.getId() ) {
            case R.id.start:
                if ( !isRunning() ) {
                    timer.start();
                    Message message = mHandler.obtainMessage();
                    mHandler.sendMessageDelayed(message, 10);
                    start_btn.setBackgroundResource(R.color.gray);
                    start_btn.setText(getResources().getString(R.string.clock_pause));
                    check_btn.setText(getResources().getString(R.string.clock_times));
                } else {
                    start_btn.setBackgroundResource(R.color.clock_restart);
                    start_btn.setText(getResources().getString(R.string.clock_restart));
                    check_btn.setText(getResources().getString(R.string.clock_reset));
                    timer.pause();
                    release();
                }
                break;
            case R.id.check_times:
                if ( isRunning() ) {
                    timer.round();
                    updateTime();
                } else {
                    timer.stop();
                    start_btn.setBackgroundResource(R.color.gray);
                    start_btn.setText(getResources().getString(R.string.clock_start));
                    updateTime();
                }
                adapter.notifyDataSetChanged();
                break;
        }
    }

    public static class MyHandler extends Handler {
        private ClockActivity activity;

        public MyHandler (ClockActivity activity) {
            this.activity = activity;
        }

        @Override
        public void handleMessage (Message msg) {
            super.handleMessage(msg);
            activity.updateTime();
            if ( activity.isRunning() ) {
                Message message = obtainMessage();
                sendMessageDelayed(message, 10);
            }
        }
    }

    @Override
    protected void onDestroy () {
        super.onDestroy();
        if ( timer != null ) {
            timer.stop();
        }
        if ( mHandler != null ) {
            mHandler.removeCallbacksAndMessages(null);
        }
        release();
    }
}
