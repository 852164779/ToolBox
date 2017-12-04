package com.newfresh.easyclean;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.CycleInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;

import com.newfresh.easyclean.view.MainActivity;


/**
 * Created by hwl on 2017/08/09.
 */

public class BaseAnimation {

    /**
     * 右上角礼物围绕中心摇动动画
     *
     * @param about
     * @return
     */
    public static RotateAnimation getShakeAnimation (View about) {
        RotateAnimation animation = new RotateAnimation(-5f, 5f, about.getWidth() / 2, about.getHeight() / 2);
        animation.setDuration(600);
        animation.setInterpolator(new CycleInterpolator(5f));
        animation.setRepeatCount(1);
        return animation;
    }

    /**
     * 进入主页的开始动画
     *
     * @param base
     * @return
     */
    public static AnimatorSet getStartShowAnimation (final MainActivity base) {

        ObjectAnimator animRam = ObjectAnimator.ofFloat(base.getRamProgress(), "percent", 0, base.getCurrentRam());
        animRam.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd (Animator animation) {
                base.setShowAnimText(0);
            }
        });

        ObjectAnimator animRom = ObjectAnimator.ofFloat(base.getRomProgress(), "percent", 0, base.getCurrentRom());

        ObjectAnimator animCpu = ObjectAnimator.ofFloat(base.getCpuProgress(), "percent", 0, base.getCurrentTemp());

        AnimatorSet animSet = new AnimatorSet();
        animSet.playTogether(animRam, animRom, animCpu);
        animSet.setDuration(1000);
        animSet.setInterpolator(new LinearInterpolator());
        animSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart (Animator animation) {
                base.sendEmptyHander(2020);
            }
        });

        return animSet;
    }


}