package com.global.toolbox;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.CycleInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;


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
    public static RotateAnimation getShakeAnimation(View about) {
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
    public static AnimatorSet getStartShowAnimation(final BaseActivity base) {

        ObjectAnimator animRam = ObjectAnimator.ofFloat(base.getRamProgress(), "percent", 0, base.getCurrentRam());
        animRam.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                base.setShowAnimText(0);
            }
        });

        ObjectAnimator animRom = ObjectAnimator.ofFloat(base.getRomProgress(), "percent", 0, base.getCurrentRom());

        ObjectAnimator animCpu = ObjectAnimator.ofFloat(base.getCpuProgress(), "percent", 0, base.getCurrentTemp());

        AnimatorSet animSet = new AnimatorSet();
        animSet.playTogether(animRam, animRom, animCpu);
        animSet.setDuration(1000);
        animSet.setInterpolator(new LinearInterpolator());

        return animSet;
    }


    public static AnimationSet get(final BaseActivity base, final View view) {

        AnimationSet proGone = new AnimationSet(true);
        proGone.addAnimation(new ScaleAnimation(1.0f, 1.2f, 1.0f, 1.2f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 1.0f));
        proGone.addAnimation(new AlphaAnimation(1.0f, 0.0f));
        proGone.addAnimation(new TranslateAnimation(0, 0, 0, -50));
        proGone.setDuration(1000);

        proGone.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                base.initWidgetData();
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


        //        view.startAnimation(proGone);
        return proGone;
    }

}