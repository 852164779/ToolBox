package com.newfresh.easyclean.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.newfresh.easyclean.BaseAnimation;
import com.newfresh.easyclean.R;
import com.newfresh.easyclean.bean.Wave;
import com.newfresh.easyclean.custom.ColorfulRingProgressView;
import com.newfresh.easyclean.custom.RippleImageView;
import com.newfresh.easyclean.custom.WaveView;
import com.newfresh.easyclean.init.BatteryService;
import com.newfresh.easyclean.light.FlashActivity;
import com.newfresh.easyclean.service.CleanService;
import com.newfresh.easyclean.util.OtherUtil;
import com.newfresh.easyclean.util.PhoneSizeUtil;
import com.newfresh.easyclean.util.XmlShareUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import c.g.z.Utils.A;


public class MainActivity extends Activity {

    private MainActivity mainThis;

    private Random random;
    private ColorfulRingProgressView ramProgress, romProgress, cpuProgress;

    private RelativeLayout romLayout, cpuLayout;
    private RippleImageView ramLayout;

    private WaveView waveView;
    private CleanService myService;
    private int bgColor, fgColor, showAnimText = 1, height, maxHeight, ramWidth, winWidth;

    private float currentRam, currentRom, currentTemp, temp, whs, ds;
    private long totalRAM, availRAM, totalROM, availROM, cacheClear;
    private TextView ramText_Pro, ramText_Pro_, ramText_Unit, ramText_Unit_, ramText_, romText_Pro, romText_, cpuText_Pro, cpuText_, total_clean;
    //    private ImageView about;
    //    private View view;
    private boolean showStartAnimator = true, showAnimator = false, showPro_ = true;

    private ArrayList<String> gameUrl = new ArrayList<>();

    private AnimatorSet animSet;


    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        //        setContentView(R.layout.base_activity_copy);
        setContentView(R.layout.activity_aopy);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar);

        mainThis = this;

        OtherUtil.hideNaviga(this);

        bindService(new Intent(this, CleanService.class), serviceConnection, Context.BIND_AUTO_CREATE);

        registerReceiver(mBatInfoReveiver, new IntentFilter(BatteryService.BatteryAction));
        random = new Random();
        //        view = getWindow().getDecorView();

        initData();
        initWidget();
        initWidgetData();

        handler.sendEmptyMessageDelayed(2020, 30 * 1000);

        ViewTreeObserver vto2 = ramProgress.getViewTreeObserver();
        vto2.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout () {
                ramProgress.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                ramWidth = ramProgress.getWidth();
            }
        });
    }


    @Override
    protected void onStart () {
        super.onStart();
        handler.sendEmptyMessage(1011);
    }

    @Override
    protected void onPause () {
        super.onPause();
    }

    @Override
    protected void onDestroy () {
        super.onDestroy();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        unregisterReceiver(mBatInfoReveiver);
        unbindService(serviceConnection);
    }

    /**
     * 初始化控件
     */
    private void initWidget () {
        ramProgress = (ColorfulRingProgressView) findViewById(R.id.ramProgress);
        romProgress = (ColorfulRingProgressView) findViewById(R.id.romProgress);
        cpuProgress = (ColorfulRingProgressView) findViewById(R.id.cpuProgress);

        romLayout = (RelativeLayout) findViewById(R.id.romLayout);
        cpuLayout = (RelativeLayout) findViewById(R.id.cpuLayout);
        ramLayout = (RippleImageView) findViewById(R.id.ramLayout);

        ramText_ = (TextView) findViewById(R.id.ramText_);
        ramText_Pro = (TextView) findViewById(R.id.ramText_Pro);
        ramText_Pro_ = (TextView) findViewById(R.id.ramText_Pro_);
        ramText_Unit = (TextView) findViewById(R.id.ramText_Unit);
        ramText_Unit_ = (TextView) findViewById(R.id.ramText_Unit_);
        total_clean = (TextView) findViewById(R.id.total_clean);

        romText_ = (TextView) findViewById(R.id.romText_);
        romText_Pro = (TextView) findViewById(R.id.romText_Pro);

        cpuText_ = (TextView) findViewById(R.id.cpuText_);
        cpuText_Pro = (TextView) findViewById(R.id.cpuText_Pro);

        //        about = (ImageView) findViewById(R.id.about);

        bgColor = ramProgress.getMBgColor();
        fgColor = ramProgress.getMFgColor();

        ramProgress.setOnPercentChengeListenner(new ColorfulRingProgressView.OnPercentChengeListenner() {
            @Override
            public void PercentChengeListening (float percent) {
                if ( showAnimText == 1 ) {
                    setText(ramText_Pro, (int) percent, true);
                } else if ( showAnimText == 2 ) {
                    setText(ramText_Pro, (int) percent, false);
                }
            }
        });

        ramProgress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                if ( !showAnimator ) {
                    showAnimator = true;

                    if ( !view.isHardwareAccelerated() ) {
                        view.setLayerType(View.LAYER_TYPE_HARDWARE, null);
                    }

                    myService.cleanAllProcess();
                    myService.setOnActionListener(new CleanService.OnPeocessActionListener() {
                        @Override
                        public void onCleanCompleted (Context context, long cacheSize) {
                            cacheClear = cacheSize;
                            if ( cacheClear <= 0 ) {
                                cacheClear = (random.nextInt(50) + 1) * 1024;
                            }
                        }
                    });

                    clickAnim.run();
                }
            }
        });

        romProgress.setOnPercentChengeListenner(new ColorfulRingProgressView.OnPercentChengeListenner() {
            @Override
            public void PercentChengeListening (float percent) {
                setText(romText_Pro, (int) percent, true);
            }
        });

        cpuProgress.setOnPercentChengeListenner(new ColorfulRingProgressView.OnPercentChengeListenner() {
            @Override
            public void PercentChengeListening (float percent) {
                setText(cpuText_Pro, (int) percent, true);
            }
        });

        /****waveView*****/
        waveView = (WaveView) findViewById(R.id.waveview);
        int color = getResources().getColor(R.color.ram_back_);
        Wave wave1 = new Wave(1080, 100, 6, 0, color);
        int color1 = getResources().getColor(R.color.ram_back);
        Wave wave2 = new Wave(1080, 100, 3, 0, color1);
        waveView.addWave(wave1);
        waveView.addWave(wave2);
    }

    public void initWidgetData () {
        //        String.format(getResources().getString(R.string.pd), Usys.getTotalCache(Usys.getTotalCache(getApplicationContext())))
        String totalStr = String.format(getResources().getString(R.string.pd), PhoneSizeUtil.getTotalCache(cacheClear));

        total_clean.setText(totalStr);

        ramProgress.setPercent(currentRam);

        if ( currentRom < 50 ) {
            romProgress.setMFgColor(getResources().getColor(R.color.warn_green));
        } else if ( currentRom >= 50 && currentRom < 80 ) {
            romProgress.setMFgColor(getResources().getColor(R.color.warn_yellow));
        } else if ( currentRom >= 80 ) {
            romProgress.setMFgColor(getResources().getColor(R.color.warn_red));
        }
        romProgress.setPercent(currentRom);

        if ( currentTemp < 30 ) {
            cpuProgress.setMFgColor(getResources().getColor(R.color.warn_green));
        } else if ( currentTemp >= 30 && currentTemp < 50 ) {
            cpuProgress.setMFgColor(getResources().getColor(R.color.warn_yellow));
        } else if ( currentTemp >= 50 ) {
            cpuProgress.setMFgColor(getResources().getColor(R.color.warn_red));
        }
        cpuProgress.setPercent(currentTemp);
    }

    private void initData () {
        totalRAM = PhoneSizeUtil.getTotalRAMSize();
        availRAM = PhoneSizeUtil.getAvailRAMSize(this);
        totalROM = PhoneSizeUtil.getTotalROMSize();
        availROM = PhoneSizeUtil.getAvailROMSize();
        temp = XmlShareUtil.getSharePreferenceLong(this, "Temperature");

        currentRam = ((int) (((float) availRAM / (float) totalRAM) * 100));
        currentRom = ((int) (((float) availROM / (float) totalROM) * 100));
        currentTemp = (int) (temp * 0.1);

        DisplayMetrics dm = getResources().getDisplayMetrics();
        maxHeight = (dm.heightPixels / 5 * 3) / 7 * 5;
        winWidth = dm.widthPixels;

        float ws = (float) dm.widthPixels / 720f;
        float hs = (float) dm.heightPixels / 1184f;
        ds = dm.density / 2.0f;
        whs = (ws + hs) / 2f;
    }

    @TargetApi (Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void setText (TextView view, long org1, boolean org2) {
        String string = "";
        int leng = String.valueOf(org1).length();
        if ( view.getId() == R.id.ramText_Pro || view.getId() == R.id.ramText_Pro_ ) {
            string = org1 + "%";

            if ( !org2 ) {
                string = PhoneSizeUtil.formatSize(org1, false);
                if ( string.endsWith("KB") || string.endsWith("MB") || string.endsWith("GB") ) {
                    string = string.substring(0, string.length() - 1);
                }
                leng = string.length() - 1;
            }

            if ( view.getId() == R.id.ramText_Pro && showPro_ ) {
                setText(ramText_Pro_, org1, org2);
            }

            ramText_Unit.setText(PhoneSizeUtil.formatSize(availRAM, false) + "/" + PhoneSizeUtil.formatSize(totalRAM, true));

        } else if ( view.getId() == R.id.romText_Pro ) {
            string = org1 + "%";
        } else if ( view.getId() == R.id.cpuText_Pro ) {
            string = org1 + "℃";
        }

        //加上伪强字符‎‎RLM(\u200E),防止切换到BiDi语言时文字左右反过来
        if ( view.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL || OtherUtil.isTextRTL(Locale.getDefault()) ) {
            string = string + PhoneSizeUtil.decodeUnicode("\\u200E");
        }
        SpannableString styledText = new SpannableString(string);

        if ( view.getId() == R.id.ramText_Pro || view.getId() == R.id.ramText_Pro_ ) {
            styledText.setSpan(new AbsoluteSizeSpan((int) (120 * whs)), 0, leng, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            styledText.setSpan(new AbsoluteSizeSpan((int) (40 * whs)), leng, leng + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else {
            styledText.setSpan(new AbsoluteSizeSpan((int) (60 * whs)), 0, leng, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            styledText.setSpan(new AbsoluteSizeSpan((int) (24 * whs)), leng, leng + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        view.setTypeface(getTextFont());
        if ( styledText != null ) {
            view.setText(styledText, TextView.BufferType.SPANNABLE);
        } else {
            view.setText(string);
        }
    }

    private Typeface getTextFont () {
        return Typeface.createFromAsset(getAssets(), "fonts/main_light.ttf");
    }

    /**
     * 进入主页的动画
     */
    Runnable loadAnimator = new Runnable() {
        @Override
        public void run () {
            ramProgress.setMBgColor(bgColor);
            ramProgress.setMFgColor(fgColor);

            animSet = BaseAnimation.getStartShowAnimation(mainThis);

            animSet.start();
        }
    };

    /**
     * 点击时候的动画
     */
    Runnable clickAnim = new Runnable() {
        @Override
        public void run () {

            AnimatorSet animSet = new AnimatorSet();
            animSet.setInterpolator(new LinearInterpolator());

            height = ramProgress.getMHeight();

            ObjectAnimator translationUp = ObjectAnimator.ofFloat(ramProgress, "X", ramProgress.getX(), ramProgress.getX() + (winWidth - ramWidth) / 2);
            translationUp.setInterpolator(new LinearInterpolator());
            translationUp.setDuration(500);
//            translationUp.addListener(new AnimatorListenerAdapter() {
//                @Override
//                public void onAnimationEnd (Animator animation) {
//                    view.setVisibility(View.GONE);
//                }
//
//                @Override
//                public void onAnimationStart (Animator animation) {
//                    view.setVisibility(View.VISIBLE);
//                }
//            });


            //圆环的大小变大
            ObjectAnimator height_ToLarge = ObjectAnimator.ofInt(ramProgress, "mHeight", height, maxHeight);
            height_ToLarge.setDuration(500);
            height_ToLarge.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart (Animator animation) {
                    romLayout.setVisibility(View.GONE);
                    cpuLayout.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationEnd (Animator animation) {
                    super.onAnimationEnd(animation);
//                    ramLayout.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

                    RelativeLayout.LayoutParams layoutParams=  new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    layoutParams.addRule(RelativeLayout.ALIGN_LEFT,R.id.ramProgress);
                    layoutParams.addRule(RelativeLayout.ALIGN_RIGHT,R.id.ramProgress);
                    layoutParams.addRule(RelativeLayout.ALIGN_TOP,R.id.ramProgress);
                    layoutParams.addRule(RelativeLayout.ALIGN_BOTTOM,R.id.ramProgress);

                    ramLayout.setLayoutParams(layoutParams);
                }
            });

            //圆环进度变成0
            currentRam = ramProgress.getPercent();

            ObjectAnimator per_ToZero = ObjectAnimator.ofFloat(ramProgress, "percent", currentRam, 0);
            per_ToZero.setDuration((long) (10 * currentRam));

            per_ToZero.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd (Animator animator) {
                    ramLayout.setVisibility(View.VISIBLE);
                    ramLayout.startWaveAnimation();
                }
            });


            //第一圈扫描
            ObjectAnimator scan_First = ObjectAnimator.ofFloat(ramProgress, "percent", 0, 100);
            scan_First.setDuration(1100);
            scan_First.setRepeatCount(1);
            scan_First.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart (Animator animation) {
                    ramProgress.setScan(true);
                }

                @Override
                public void onAnimationEnd (Animator animator) {
                    ramProgress.setMBgColor(bgColor);
                    ramProgress.setMFgColor(bgColor);
                }

                @Override
                public void onAnimationRepeat (Animator animation) {
                    ramProgress.setMBgColor(fgColor);
                    ramProgress.setMFgColor(bgColor);
                }
            });

            //第二圈扫描
            ObjectAnimator scan_Second = ObjectAnimator.ofFloat(ramProgress, "percent", 0, 100);
            scan_Second.setRepeatCount(1);
            scan_Second.setDuration(1100);
            scan_Second.setStartDelay(500);
            scan_Second.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart (Animator animation) {
                    ramProgress.setMBgColor(bgColor);
                    ramProgress.setMFgColor(fgColor);
                }

                @Override
                public void onAnimationEnd (Animator animator) {
                    ramLayout.setVisibility(View.GONE);
                    ramLayout.stopWaveAnimation();
                    ramProgress.setScan(false);

                    //                    handler.sendEmptyMessage(1024);
                    clearAnimatorThree.run();
                }

                @Override
                public void onAnimationRepeat (Animator animation) {
                    ramProgress.setMBgColor(fgColor);
                    ramProgress.setMFgColor(bgColor);
                }
            });

            animSet.play(height_ToLarge);
//            animSet.play(translationUp).with(height_ToLarge);
            animSet.play(per_ToZero).after(height_ToLarge);
            animSet.play(scan_First).after(per_ToZero);
            animSet.play(scan_Second).after(scan_First);

            animSet.start();

        }
    };

    /**
     * 清理的垃圾数逐渐变大
     */
    Runnable clearAnimatorThree = new Runnable() {
        @Override
        public void run () {
            ramProgress.setMBgColor(bgColor);
            ramProgress.setMFgColor(bgColor);
            showAnimText = 2;

            showPro_ = false;

            final AnimationSet proGone = new AnimationSet(true);
            proGone.addAnimation(new ScaleAnimation(1.0f, 1.2f, 1.0f, 1.2f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 1.0f));
            proGone.addAnimation(new AlphaAnimation(1.0f, 0.0f));
            proGone.addAnimation(new TranslateAnimation(0, 0, 0, -50));
            proGone.setDuration(1000);

            proGone.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart (Animation animation) {
                    initWidgetData();
                }

                @Override
                public void onAnimationEnd (Animation animation) {
                    ramText_Pro_.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat (Animation animation) {

                }
            });
            ramText_Pro_.startAnimation(proGone);

            final AnimationSet proVisi = new AnimationSet(true);
            proVisi.addAnimation(new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 1.0f));
            proVisi.addAnimation(new AlphaAnimation(0.0f, 1.0f));
            proVisi.setDuration(1000);
            ramText_Pro.startAnimation(proVisi);


            ObjectAnimator anim = ObjectAnimator.ofFloat(ramProgress, "percent", 0, cacheClear);
            anim.setInterpolator(new LinearInterpolator());
            anim.setDuration(1000);
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd (Animator animator) {
                    showPro_ = true;
                    showAnimText = 0;

                    //                    handler.sendEmptyMessageDelayed(1025, 1000);
                    clearAnimatorFour.run();

                    initData();
                }

            });

            anim.start();

            final AnimationSet unitVisi = new AnimationSet(true);
            unitVisi.addAnimation(new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 1.0f));
            unitVisi.addAnimation(new AlphaAnimation(0.0f, 1.0f));
            unitVisi.setDuration(400);

            AnimationSet unitGone = new AnimationSet(true);
            unitGone.addAnimation(new ScaleAnimation(1.0f, 0.0f, 1.0f, 0.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 1.0f));
            unitGone.addAnimation(new AlphaAnimation(1.0f, 0.0f));
            unitGone.setDuration(400);
            unitGone.setStartOffset(600);
            unitGone.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart (Animation animation) {

                }

                @Override
                public void onAnimationEnd (Animation animation) {
                    ramText_Unit.setVisibility(View.GONE);
                    ramText_Unit_.setTextColor(getResources().getColor(R.color.base_text));
                    ramText_Unit_.setVisibility(View.VISIBLE);
                    ramText_Unit_.startAnimation(unitVisi);
                }

                @Override
                public void onAnimationRepeat (Animation animation) {

                }
            });
            ramText_Unit.startAnimation(unitGone);
        }
    };


    /**
     * 圆环高度变回原高度
     */
    Runnable clearAnimatorFour = new Runnable() {
        @Override
        public void run () {

            AnimatorSet animSet = new AnimatorSet();

            ObjectAnimator anim = ObjectAnimator.ofInt(ramProgress, "mHeight", maxHeight, height);
            anim.setInterpolator(new LinearInterpolator());
            anim.setDuration(500);
            anim.setStartDelay(1000);
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd (Animator animator) {
                    romLayout.setVisibility(View.VISIBLE);
                    cpuLayout.setVisibility(View.VISIBLE);
                    handler.sendEmptyMessage(1012);
                    //                    handler.sendEmptyMessageDelayed(1029, 1100);
                    cacheGone.run();

                }
            });

            animSet.play(anim);

            animSet.start();
        }
    };

    /**
     * 清理的垃圾数字消失
     */
    Runnable cacheGone = new Runnable() {
        @Override
        public void run () {
            //清理的垃圾数目消失
            AnimationSet proGone = new AnimationSet(true);
            proGone.addAnimation(new ScaleAnimation(1.0f, 0.0f, 1.0f, 0.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 1.0f));
            proGone.addAnimation(new AlphaAnimation(1.0f, 0.0f));
            proGone.setDuration(800);
            proGone.setStartTime(1100);
            proGone.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart (Animation animation) {
                    setText(ramText_Pro_, cacheClear, false);
                }

                @Override
                public void onAnimationEnd (Animation animation) {
                }

                @Override
                public void onAnimationRepeat (Animation animation) {
                }
            });
            ramText_Pro_.startAnimation(proGone);

            //百分比出现
            AnimationSet proVisi = new AnimationSet(true);
            proVisi.addAnimation(new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 1.0f));
            proVisi.addAnimation(new AlphaAnimation(0.0f, 1.0f));
            proVisi.setDuration(800);
            proVisi.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart (Animation animation) {
                    setText(ramText_Pro, (int) currentRam, true);
                }

                @Override
                public void onAnimationEnd (Animation animation) {
                    showAnimator = false;
                }

                @Override
                public void onAnimationRepeat (Animation animation) {
                }
            });
            ramText_Pro.startAnimation(proVisi);


            //总RAM出现
            final AnimationSet unitVisi = new AnimationSet(true);
            unitVisi.addAnimation(new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 1.0f));
            unitVisi.addAnimation(new AlphaAnimation(0.0f, 1.0f));
            unitVisi.setDuration(400);

            //已清理消失
            AnimationSet unitGone = new AnimationSet(true);
            unitGone.addAnimation(new ScaleAnimation(1.0f, 0.0f, 1.0f, 0.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 1.0f));
            unitGone.addAnimation(new AlphaAnimation(1.0f, 0.0f));
            unitGone.setDuration(400);
            unitGone.setStartOffset(400);
            unitGone.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart (Animation animation) {

                }

                @Override
                public void onAnimationEnd (Animation animation) {
                    ramText_Unit_.setTextColor(getResources().getColor(R.color.transparent));
                    ramText_Unit.setVisibility(View.VISIBLE);
                    ramText_Unit.startAnimation(unitVisi);
                }

                @Override
                public void onAnimationRepeat (Animation animation) {

                }
            });
            ramText_Unit_.startAnimation(unitGone);

        }
    };

    Handler handler = new Handler() {
        @Override
        public void handleMessage (Message msg) {
            super.handleMessage(msg);
            if ( msg.what == 1011 ) {
                if ( animSet != null ) {
                    if ( animSet.isRunning() || animSet.isStarted() ) {
                        animSet.cancel();
                        showStartAnimator = true;
                    }
                }

                if ( showStartAnimator ) {
                    showAnimText = 1;
                    loadAnimator.run();
                }
                showStartAnimator = true;
            } else if ( msg.what == 1012 ) {
                if ( showStartAnimator ) {
                    showAnimText = 0;
                    loadAnimator.run();
                }
                showStartAnimator = true;
            } else if ( msg.what == 2020 ) {
                handler.removeMessages(2020);
                handler.sendEmptyMessageDelayed(2020, 30 * 1000);
            }
        }
    };


    public void baseOnClick (View view) {
        showStartAnimator = false;
        switch ( view.getId() ) {
            case R.id.battery:
                startActivity(new Intent(MainActivity.this, BatteryActivity.class));
                break;
            case R.id.light:
                OtherUtil.releas_flash_(this);
                startActivity(new Intent(MainActivity.this, FlashActivity.class));
                break;
            case R.id.calculator:
                startActivity(new Intent(MainActivity.this, CalculatorActivity.class));
                break;
            case R.id.clock:
                startActivity(new Intent(MainActivity.this, ClockActivity.class));
                break;
            case R.id.camera:
                OtherUtil.releas_flash_(this);
                startActivity(new Intent(MainActivity.this, CameraActivity.class));
                break;
            case R.id.mirror:
                OtherUtil.releas_flash_(this);
                startActivity(new Intent(MainActivity.this, MirrorActivity.class));
                break;
        }

        A.getInstance(this).showDialog();

        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    public ColorfulRingProgressView getCpuProgress () {
        return cpuProgress;
    }

    public ColorfulRingProgressView getRamProgress () {
        return ramProgress;
    }

    public ColorfulRingProgressView getRomProgress () {
        return romProgress;
    }

    public RelativeLayout getRomLayout () {
        return romLayout;
    }

    public RelativeLayout getCpuLayout () {
        return cpuLayout;
    }

    public float getCurrentRam () {
        return currentRam;
    }

    public float getCurrentRom () {
        return currentRom;
    }

    public float getCurrentTemp () {
        return currentTemp;
    }

    public int getRamRightHeight () {
        return height;
    }

    public int getRamMaxHeight () {
        return maxHeight;
    }

    public void setShowAnimText (int showAnimText) {
        this.showAnimText = showAnimText;
    }

    public void sendEmptyHander (int what) {
        handler.sendEmptyMessage(what);
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected (ComponentName componentName, IBinder iBinder) {
            CleanService.ProcessServiceBinder binder = (CleanService.ProcessServiceBinder) iBinder;
            myService = binder.getService();
        }

        @Override
        public void onServiceDisconnected (ComponentName componentName) {
        }
    };

    private BroadcastReceiver mBatInfoReveiver = new BroadcastReceiver() {
        @Override
        public void onReceive (Context context, Intent intent) {
            if ( BatteryService.BatteryAction.equals(intent.getAction()) ) {
                if ( !showAnimator ) {
                    try {
                        JSONObject battery = new JSONObject(intent.getStringExtra("battery"));
                        temp = battery.getInt("temperature");

                        totalRAM = PhoneSizeUtil.getTotalRAMSize();
                        availRAM = PhoneSizeUtil.getAvailRAMSize(getApplicationContext());
                        totalROM = PhoneSizeUtil.getTotalROMSize();
                        availROM = PhoneSizeUtil.getAvailROMSize();

                        currentRam = ((float) availRAM / (float) totalRAM) * 100;
                        currentRom = ((float) availROM / (float) totalROM) * 100;
                        currentTemp = (float) (temp * 0.1);

                        initWidgetData();

                        Map<String, Long> data = new HashMap<>();
                        data.put("TotalRAM", totalRAM);
                        data.put("AvailRAM", availRAM);
                        data.put("TotalROM", totalROM);
                        data.put("AvailROM", availROM);
                        data.put("Temperature", (long) temp);

                        XmlShareUtil.saveSharedInfor(getApplicationContext(), data);

                    } catch ( Exception e ) {
                        e.printStackTrace();
                    }
                }
            }
        }
    };
}