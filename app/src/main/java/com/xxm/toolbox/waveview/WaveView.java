package com.xxm.toolbox.waveview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Senh Linsh on 17/3/6.
 */

public class WaveView extends View {

    protected static int sDefaultColor = 0x663F51B5;
    private List<Wave> mWaves;
    private Runnable mWaveRun;
    private boolean mShouldRefreshPath = true;
    private Orientation mOrientation;

    public WaveView(Context context) {
        super(context);
        init();
    }

    public WaveView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WaveView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

        viewH = getHeight();

        mWaves = new ArrayList<>();
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
//        if (isInEditMode()) {
//            LinearGradient shader = new LinearGradient(0, 0, 0, 600, 0xFF303F9F, 0x553F51B5, Shader.TileMode.CLAMP);
//            PercentWave wave1 = new PercentWave(1.50f, 0.03f, 0.018f, 0.26f, shader);
//            PercentWave wave2 = new PercentWave(1.28f, 0.02f, -0.02f, 0.31f, shader);
//            PercentWave wave3 = new PercentWave(2.08f, 0.01f, 0.01f, 0.49f, shader);
//            addWave(wave1);
//            addWave(wave2);
//            addWave(wave3);
//            mOrientation = Orientation.DOWN;
//        }
    }

    public void addWave(Wave wave) {
        mWaves.add(wave);
    }

    public void addWave(int waveWidth, int waveHeight, int speed, int yOff, int color) {
        mWaves.add(new Wave(waveWidth, waveHeight, speed, yOff, color));
    }

    public void removeWave(Wave wave) {
        mWaves.remove(wave);
    }

    public void removeWave(int index) {
        if (index >= 0 && index < mWaves.size()) {
            mWaves.remove(index);
        }
    }

    public void setDefaultColor(int color) {
        sDefaultColor = color;
    }

    public void moveWaves() {
        if (getVisibility() != VISIBLE) return;

        if (mWaveRun == null) {
            mWaveRun = new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < mWaves.size(); i++) {
                        Wave wave = mWaves.get(i);
                        Path path = wave.path;
                        int speed = wave.speed == 0 ? 1 : wave.speed;
                        path.offset(speed, 0);
                        wave.startX += speed;
                        if (wave.startX > 0) {
                            wave.startX -= wave.waveWidth;
                            path.offset(-wave.waveWidth, 0);
                        } else if (wave.startX <= -wave.waveWidth) {
                            wave.startX += wave.waveWidth;
                            path.offset(wave.waveWidth, 0);
                        }
                    }
                    invalidate();
                    moveWaves();
                }
            };
        }
        removeCallbacks(mWaveRun);

        postDelayed(mWaveRun, 1);
    }

    public void stopWaves() {
        removeCallbacks(mWaveRun);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);

        viewIndex = viewIndex + 10;

        viewH = getHeight() / 2 - viewIndex;

        for (int i = 0; i < mWaves.size(); i++) {

            Wave wave = mWaves.get(i);

            Path path = wave.path;

            path.reset();

            //if (shouldRefreshPath(wave)) {

            if (viewH <= wave.getWaveHeight()) viewH = wave.getWaveHeight() / 2;

            refreshPath1(wave);

            //wave.shouldRefreshPath = false;
            //}
            canvas.drawPath(path, wave.paint);
        }

        mShouldRefreshPath = false;
    }

    // 控件宽高改变或者Wave的参数改变, 都应该重新绘制路径
    private boolean shouldRefreshPath(Wave wave) {
        return mShouldRefreshPath || wave.shouldRefreshPath;
    }

    private int viewH;

    private int viewIndex;

    // 重新绘制路径
    private Path refreshPath1(Wave wave) {
        int viewWidth = getWidth();
        int viewHeight = getHeight();
        Path path = wave.path;
        // 检查波浪曲线参数, 给没有取值的参数赋默认值
        wave.checkWave(viewWidth, viewHeight);
        int waveWidth = wave.waveWidth;
        int waveHeight = wave.waveHeight;
        int yOff = mOrientation == Orientation.DOWN ? wave.yOff : -wave.yOff;
        int waveNum = (viewWidth - 1) / wave.waveWidth + 2;
        int startX = wave.startX;
        path.moveTo(startX, viewH + yOff);
        for (int j = 0; j < waveNum; j++) {
            int curWaveStartX = startX + (j * waveWidth);
            int firstControlX = curWaveStartX + waveWidth / 4;
            int firstControlY = viewH + waveHeight / 2 + yOff;
            int secondControlX = curWaveStartX + waveWidth * 3 / 4;
            int secondControlY = viewH - waveHeight / 2 + yOff;
            path.quadTo(firstControlX, firstControlY, curWaveStartX + waveWidth / 2, viewH + yOff);
            path.quadTo(secondControlX, secondControlY, curWaveStartX + waveWidth, viewH + yOff);
            path.rQuadTo(startX, viewH, 0, 0);
            path.rQuadTo(startX, -viewH, 0, 0);
        }
        path.lineTo(waveWidth, viewHeight);
        path.lineTo(0, viewHeight);
        path.close();

        return path;
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        moveWaves();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeCallbacks(mWaveRun);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mShouldRefreshPath = true;
    }

    public void setOrientation(Orientation orientation) {
        mOrientation = orientation;
    }

    public enum Orientation {
        UP, DOWN
    }


}
