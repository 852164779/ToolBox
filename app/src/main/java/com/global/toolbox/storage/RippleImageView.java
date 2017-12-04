package com.global.toolbox.storage;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.global.toolbox.R;

/**
 * Created by Admin on 2017/6/20.
 */

public class RippleImageView extends RelativeLayout {
    private static final int SHOW_SPACING_TIME = 1700;
    /**
     * 三张波纹图片
     */
    private static final int SIZE = 3;

    /**
     * 动画默认循环播放时间
     */
    private int show_spacing_time = SHOW_SPACING_TIME;
    /**
     * 初始化动画集
     */
    private AnimationSet[] mAnimationSet = new AnimationSet[SIZE];
    /**
     * 水波纹图片
     */
    private ImageView[] imgs = new ImageView[SIZE];
    /**
     * 水波纹和背景图片的大小
     */
    private float imageViewWidth = 0;
    private float imageViewHeigth = 0;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            imgs[msg.what].startAnimation(mAnimationSet[msg.what]);
        }
    };

    public RippleImageView(Context context) {
        super(context);
        initView(context);
    }

    public RippleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int mheight = (int) ((dm.heightPixels  / 5 * 3) / 7 * 4 * 1.3);//

        show_spacing_time = SHOW_SPACING_TIME;
        imageViewWidth = dm.widthPixels;
        imageViewHeigth = mheight;

        setLayout(context);
        for (int i = 0; i < imgs.length; i++) {
            mAnimationSet[i] = initAnimationSet();
        }
    }

    /**
     * 开始动态布局
     */
    private void setLayout(Context context) {
        LayoutParams params = new LayoutParams((int)imageViewWidth*2, (int)imageViewHeigth*2);
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);

        /**添加水波纹图片*/
        for (int i = 0; i < SIZE; i++) {
            imgs[i] = new ImageView(context);
            imgs[i].setImageResource(R.drawable.icon_ripple);
            addView(imgs[i], params);
        }
    }

    /**
     * 初始化动画集
     *
     * @return
     */
    private AnimationSet initAnimationSet() {
        AnimationSet as = new AnimationSet(true);
        //缩放度：变大两倍
        ScaleAnimation sa = new ScaleAnimation(1f, 1.5f, 1f, 1.5f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
        sa.setDuration(show_spacing_time * 3);

        //透明度
        AlphaAnimation aa = new AlphaAnimation(0.6f, 0.0f);
        aa.setDuration(show_spacing_time * 3);
        aa.setFillAfter(true);

        as.addAnimation(sa);
        as.addAnimation(aa);
        return as;
    }

    /**
     * 开始水波纹动画
     */
    public void startWaveAnimation() {
        for (int i = 0; i < SIZE; i++) {
            mAnimationSet[i].setStartOffset(show_spacing_time * i);
            imgs[i].startAnimation(mAnimationSet[i]);
        }
    }

    /**
     * 停止水波纹动画
     */
    public void stopWaveAnimation() {
        for (int i = 0; i < imgs.length; i++) {
            imgs[i].clearAnimation();
        }
    }
}