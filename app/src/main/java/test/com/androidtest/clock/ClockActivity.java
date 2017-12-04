package test.com.androidtest.clock;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import test.com.androidtest.R;

/**
 * Created by xlc on 2016/11/29.
 */
public class ClockActivity extends Activity implements View.OnClickListener {

    private TextView start_btn, check_btn;

    private TimeView mTimeView;

    private Timer timer;

    private MyHandler mHandler;

    private ClockAdapter adapter;

    private ListView listView;

    private List<Clock> list;

    private MediaPlayer mp;//mediaPlayer对象

    public boolean isRunning() {
        return timer.isRunning();
    }

    public void updateTime() {
        timer.update();
        TwoTuple<Long, Long> result = timer.getNowTime();
        //mCircleView.changeProgress((float) result._1 / 600f);
        mTimeView.setTime(result._2);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.clock_activity);

        timer = new Timer(getApplicationContext());

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


    private void play() {
        try {
            if (mp != null) ;
            mp.reset();
            mp = MediaPlayer.create(ClockActivity.this, R.raw.time);//重新设置要播放的音频
            mp.start();//开始播放
        } catch (Exception e) {
            e.printStackTrace();//输出异常信息
        }
        if (mp != null) {
            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer arg0) {
                    // TODO Auto-generated method stub
                    play();//重新开始播放
                }
            });
        }
    }

    private void release() {
        if (mp != null) {
            mp.release();
            mp = null;
        }
    }

    @Override
    public void onClick(View v) {


        switch (v.getId()) {
            case R.id.start:

                if (!isRunning()) {
                   // play();
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
                if (isRunning()) {
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

        public MyHandler(ClockActivity activity) {
            this.activity = activity;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            activity.updateTime();
            if (activity.isRunning()) {
                Message message = obtainMessage();
                sendMessageDelayed(message, 10);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.stop();
        }
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        release();
    }
}