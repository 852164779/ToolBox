package com.global.toolbox.storage;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

import com.global.toolbox.R;

public class ColorfulRingProgressView extends View {

    private float mPercent = 75;
    private float mStrokeWidth;
    private int mBgColor = 0xffe1e1e1;
    private float mStartAngle = 0;
    private int mFgColor = 0xffff4800;

    /**
     * 1:大圆 2:小圆
     */
    private float type = 1;

    private SweepGradient sgShader;
    private RectF mOval;
    private Paint mPaint;
    private Paint scanPaint;
    private Matrix sacnMat;
    private boolean scan = false;
    private int[] sgColor;
    private int mHeight = 0, winHeight = 0;

    private OnPercentChengeListenner percentChengeListenner = null;


    public ColorfulRingProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ColorfulRingProgressView, 0, 0);
        try {
            type = a.getFloat(R.styleable.ColorfulRingProgressView_type, 0);
        } finally {
            a.recycle();
        }

        DisplayMetrics dm = getResources().getDisplayMetrics();
        float ws = (float) dm.widthPixels / 720f;
        float hs = (float) dm.heightPixels / 1184f;
        float ds = dm.density / 2.0f;
        float whs = (ws + hs) / 2f;
        winHeight = dm.heightPixels;//50是标题栏的高度

        if (type == 1) {
            mHeight = (winHeight / 5 * 3) / 28 * 18;   //  7/4.5
        } else {
            mHeight = (winHeight / 5 * 3) / 28 * 7;  // 7/1.75
        }

        mFgColor = getResources().getColor(R.color.ram_back);
        mBgColor = getResources().getColor(R.color.ring_back);
        mPercent = 75;
        mStartAngle = 270 + 180;
        if (type == 1) {
            mStrokeWidth = 36 * whs;
        } else {
            mStrokeWidth = 8 * whs;
        }

        init();
    }

    private void init() {
        mPaint = new Paint();
        scanPaint = new Paint();
        sacnMat = new Matrix();

        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mStrokeWidth);

        sgColor = new int[]{getResources().getColor(R.color.white), getResources().getColor(R.color.ram_back), getResources().getColor(R.color.ram_back)};
        sgShader = new SweepGradient(mHeight / 2, mHeight / 2, sgColor, null);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (scan) {
            scanPaint.setColor(getResources().getColor(R.color.white));
            scanPaint.setShader(null);
            canvas.drawArc(mOval, mStartAngle, 360, true, scanPaint);

            sacnMat.reset();
            scanPaint.setShader(sgShader);
            if (mPercent * 3.6f > 45) {
                sacnMat.preRotate(mStartAngle + mPercent * 3.6f - 46, mHeight / 2, mHeight / 2);//渲染开始的角度
                sgShader.setLocalMatrix(sacnMat);
                canvas.drawArc(mOval, mStartAngle + (mPercent * 3.6f - 45), 45, true, scanPaint);
            } else {
                sacnMat.preRotate(mStartAngle - 1, mHeight / 2, mHeight / 2);//
                sgShader.setLocalMatrix(sacnMat);
                canvas.drawArc(mOval, mStartAngle, mPercent * 3.6f, true, scanPaint);
            }
        }

        mPaint.setStrokeWidth(mStrokeWidth - 2);
        mPaint.setShader(null);
        mPaint.setColor(mBgColor);
        canvas.drawArc(mOval, 0, 360, false, mPaint);

        mPaint.setStrokeWidth(mStrokeWidth);
        mPaint.setColor(mFgColor);
        canvas.drawArc(mOval, mStartAngle, mPercent * 3.6f, false, mPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(mHeight, mHeight);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        updateOval();
    }

    public void setScan(boolean scan) {
        this.scan = scan;
    }

    public int getMBgColor() {
        return mBgColor;
    }

    public void setMBgColor(int mBgColor) {
        this.mBgColor = mBgColor;
    }

    public float getPercent() {
        return mPercent;
    }

    public void setPercent(float mPercent) {
        this.mPercent = mPercent;
        percentChengeListenner.PercentChengeListening(mPercent);
        refreshTheLayout();
    }

    private void updateOval() {
        mOval = new RectF(mStrokeWidth, mStrokeWidth, getWidth() - mStrokeWidth, getHeight() - mStrokeWidth);
    }


    public int getMHeight() {
        return mHeight;
    }

    //主页属性动画需要调用
    public void setMHeight(int mHeight) {
        this.mHeight = mHeight;
        setMeasuredDimension(mHeight, mHeight);
        refreshTheLayout();
    }

    public void refreshTheLayout() {
        invalidate();
        requestLayout();
    }

    public int getMFgColor() {
        return mFgColor;
    }

    public void setMFgColor(int mFgColor) {
        this.mFgColor = mFgColor;
        updateOval();
        refreshTheLayout();
    }

    public void setOnPercentChengeListenner(OnPercentChengeListenner percentChengeListenner) {
        this.percentChengeListenner = percentChengeListenner;
    }

    public interface OnPercentChengeListenner {
        void PercentChengeListening (float percent);
    }
}