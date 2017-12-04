package com.global.toolbox.clock;


import android.content.Context;

import com.global.toolbox.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by sunsai on 2016/1/28.
 */
public class Timer {

    private long time;
    private long beginTime;
    private long sumTime;
    private long oneSubTime;
    private long count;
    public boolean running;

    private List<Clock> subTimes;

    private SimpleDateFormat simpleDateFormat;

    private Context context;

    public Timer(Context c) {
        simpleDateFormat = new SimpleDateFormat("mm:ss");
        this.context=c;
        init();
    }

    /**
     * 开始
     */
    public void start() {
        running = true;
        beginTime = new Date().getTime();
    }

    /**
     * 继续
     */
    public void conti() {
        if (!isRunning()) {
            running = true;
            beginTime = new Date().getTime();
        }
    }

    /**
     * 暂停
     */
    public void pause() {
        running = false;
        sumTime = sumTime + _updateTime();
        oneSubTime = oneSubTime + time;
        time = 0;
    }

    /**
     * 停止，既 重置
     * 只有在暂停状态下可以重置
     */
    public void stop() {
        if (!isRunning()) {
            init();
        }
    }


    /**
     * 从开始到现在所用的时间，单位毫秒
     * 或者， 从上次暂停到现在的时间
     *
     * @return
     */
    private long _updateTime() {
        long now = new Date().getTime();
        time = now - beginTime;
        return time;
    }

    /**
     * 记一圈用时
     */
    public void round() {
        if (isRunning()) {
            long t = _updateTime();

            long subT = oneSubTime + t;
            long allT = sumTime + t;
            Clock c = new Clock(context.getResources().getString(R.string.clock_times)+" " + (count + 1), "+" + toStrTime(subT), toStrTime(allT));
            subTimes.add(c);
            count++;
            oneSubTime = 0;
            sumTime += time;
            time = 0;
            beginTime = new Date().getTime();
        }
    }

    public List<Clock> getList() {
        return subTimes;
    }

    public String getOneItem(long roundTime, long allTime) {

        return "#" + count + "    " + toStrTime(roundTime) + "    " + toStrTime(allTime);
    }

    /**
     * @return _1 是一圈的时间， _2 是目前总的时间
     */
    public TwoTuple<Long, Long> getNowTime() {
        update();
        long t = time;
        return new TwoTuple<>(oneSubTime + t, sumTime + t);
    }

    /**
     * 更新时间
     */
    public void update() {
        if (isRunning()) {
            _updateTime();
        }
    }

    public boolean isRunning() {
        return running;
    }

    public void init() {
        running = false;
        time = 0;
        beginTime = 0;
        sumTime = 0;
        oneSubTime = 0;
        count = 0;
        if (null == subTimes) {
            subTimes = new ArrayList<>();
        } else {
            subTimes.clear();
        }
    }

    /**
     * 转换时间格式
     *
     * @param t
     * @return
     */
    public String toStrTime(long t) {

        int fen= (int)t/60000;

        int miao=((int)t%60000)/1000;

        return fen+":"+miao+ ":" + ((int) t % 1000) / 10;
    }


}