package com.newfresh.easyclean.bean;

import android.graphics.Paint;
import android.graphics.Path;

import com.newfresh.easyclean.custom.WaveView;

public class Wave {
    private int startX;
    private int waveWidth;
    private int waveHeight;
    private int speed;
    private int yOff;

    private Paint paint;
    private Path path;
    private boolean shouldRefreshPath;

    public Wave (int waveWidth, int waveHeight, int speed, int yOff, int color) {
        this.waveWidth = waveWidth;
        this.waveHeight = waveHeight;
        this.speed = speed;
        this.yOff = yOff;
        path = new Path();
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(color == 0 ? WaveView.sDefaultColor : color);
        shouldRefreshPath = true;
    }

    public int getStartX () {
        return startX;
    }

    public void setStartX (int startX) {
        this.startX = startX;
    }

    public int getWaveWidth () {
        return waveWidth;
    }

    public void setWaveWidth (int waveWidth) {
        this.waveWidth = waveWidth;
    }

    public void setWaveHeight (int waveHeight) {
        this.waveHeight = waveHeight;
    }

    public int getSpeed () {
        return speed;
    }

    public void setSpeed (int speed) {
        this.speed = speed;
    }

    public int getyOff () {
        return yOff;
    }

    public void setyOff (int yOff) {
        this.yOff = yOff;
    }

    public Paint getPaint () {
        return paint;
    }

    public void setPaint (Paint paint) {
        this.paint = paint;
    }

    public Path getPath () {
        return path;
    }

    public void setPath (Path path) {
        this.path = path;
    }

    public boolean getShouldRefreshPath () {
        return shouldRefreshPath;
    }

    public void setShouldRefreshPath (boolean shouldRefreshPath) {
        this.shouldRefreshPath = shouldRefreshPath;
    }

    public int getWaveHeight () {
        return waveHeight;
    }

    public void setColor (int color) {
        if ( paint.getColor() != color ) {
            paint.setColor(color);
            shouldRefreshPath = true;
        }
    }

    public void checkWave (int viewWidth, int viewHeight) {
        if ( waveWidth <= 0 ) {
            waveWidth = viewWidth;
        }
        if ( waveHeight < 0 ) {
            waveHeight = viewHeight;
        }
    }
}