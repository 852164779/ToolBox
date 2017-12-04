package com.clgd.toolbox;

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
import android.graphics.Color;
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
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.CycleInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.clgd.toolbox.about.XLCPlug;
import com.clgd.toolbox.battery.BatteryActivity;
import com.clgd.toolbox.calculator.CalculatorActivity;
import com.clgd.toolbox.camera.CameraActivity;
import com.clgd.toolbox.camera.MirrorActivity;
import com.clgd.toolbox.clean.Cb;
import com.clgd.toolbox.clock.ClockActivity;
import com.clgd.toolbox.light.FlashActivity;
import com.clgd.toolbox.storage.ColorfulRingProgressView;
import com.clgd.toolbox.storage.RippleImageView;
import com.clgd.toolbox.util.Usys;
import com.clgd.toolbox.util.Utils;
import com.clgd.toolbox.waveview.Wave;
import com.clgd.toolbox.waveview.WaveView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import a.d.b.SubSdk;


public class BaseActivity extends Activity {

    private Random random;
    private ColorfulRingProgressView ramProgress, romProgress, cpuProgress;

    private RelativeLayout romLayout, cpuLayout;

    private RippleImageView ramLayout;
    private Cb myService;
    private int bgColor, fgColor, showAnimText = 1, height, maxHeight;

    private float currentRam, currentRom, currentTemp, temp, health, whs, ds;
    private long totalRAM, availRAM, totalROM, availROM, cacheClear;
    private TextView ramText_Pro, ramText_Pro_, ramText_Unit, ramText_Unit_, ramText_, romText_Pro, romText_, cpuText_Pro, cpuText_, total_clean;
    private ImageView about;
    private View view;
    private boolean showStartAnimator = true, showAnimator = false, showPro_ = true;

    private ArrayList<String> gameUrl = new ArrayList<>();

    private int text = 0;

    private AnimatorSet animSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.base_activity_copy);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar);

        Utils.hideNaviga(this);

        bindService(new Intent(this, Cb.class), serviceConnection, Context.BIND_AUTO_CREATE);

        registerReceiver(mBatInfoReveiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        random = new Random();
        view = getWindow().getDecorView();

        initData();
        initWidget();
        initWidgetData();

        //右上角跳转需要用到
        initGameURL();

        handler.sendEmptyMessageDelayed(2020, 30 * 1000);
    }

    @Override
    protected void onStart() {
        super.onStart();
        handler.sendEmptyMessage(1011);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        unregisterReceiver(mBatInfoReveiver);
        unbindService(serviceConnection);
    }

    /**
     * 初始化控件
     */
    private void initWidget() {
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

        about = (ImageView) findViewById(R.id.about);

        bgColor = ramProgress.getMBgColor();
        fgColor = ramProgress.getMFgColor();

        ramProgress.setOnPercentChengeListenner(new ColorfulRingProgressView.OnPercentChengeListenner() {
            @Override
            public void PercentChengeListening(float percent) {
                if (showAnimText == 1) {
                    setText(ramText_Pro, (int) percent, true);
                } else if (showAnimText == 2) {
                    setText(ramText_Pro, (int) percent, false);
                }
            }
        });

        ramProgress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!showAnimator) {
                    showAnimator = true;

                    if (!view.isHardwareAccelerated()) {
                        view.setLayerType(View.LAYER_TYPE_HARDWARE, null);
                    }

                    myService.cleanAllProcess();
                    myService.setOnActionListener(new Cb.OnPeocessActionListenerAdapter() {
                        @Override
                        public void onCleanCompleted(Context context, long cacheSize) {
                            cacheClear = cacheSize;
                            if (cacheClear <= 0) {
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
            public void PercentChengeListening(float percent) {
                setText(romText_Pro, (int) percent, true);
            }
        });

        cpuProgress.setOnPercentChengeListenner(new ColorfulRingProgressView.OnPercentChengeListenner() {
            @Override
            public void PercentChengeListening(float percent) {
                setText(cpuText_Pro, (int) percent, true);
            }
        });

        /****waveView*****/
        WaveView waveView = (WaveView) findViewById(R.id.waveview);
        int color = Color.parseColor("#b3eefc");
        Wave wave1 = new Wave(1080, 100, 6, 0, color);
        int color1 = Color.parseColor("#3bacec");
        Wave wave2 = new Wave(1080, 100, 3, 0, color1);
        waveView.addWave(wave1);
        waveView.addWave(wave2);
    }

    public void initWidgetData() {
        //        String.format(getResources().getString(R.string.pd), Usys.getTotalCache(Usys.getTotalCache(getApplicationContext())))
        String totalStr = String.format(getResources().getString(R.string.pd), Usys.getTotalCache(cacheClear));

        total_clean.setText(totalStr);

        ramProgress.setPercent(currentRam);

        if (currentRom < 50) {
            romProgress.setMFgColor(getResources().getColor(R.color.warn_green));
        } else if (currentRom >= 50 && currentRom < 80) {
            romProgress.setMFgColor(getResources().getColor(R.color.warn_yellow));
        } else if (currentRom >= 80) {
            romProgress.setMFgColor(getResources().getColor(R.color.warn_red));
        }
        romProgress.setPercent(currentRom);

        if (currentTemp < 30) {
            cpuProgress.setMFgColor(getResources().getColor(R.color.warn_green));
        } else if (currentTemp >= 30 && currentTemp < 50) {
            cpuProgress.setMFgColor(getResources().getColor(R.color.warn_yellow));
        } else if (currentTemp >= 50) {
            cpuProgress.setMFgColor(getResources().getColor(R.color.warn_red));
        }
        cpuProgress.setPercent(currentTemp);
    }

    private void initData() {
        totalRAM = Usys.getTotalRAMSize();
        availRAM = Usys.getAvailRAMSize(this);
        totalROM = Usys.getTotalROMSize();
        availROM = Usys.getAvailROMSize();
        temp = Usys.getSharePreferenceLong(this, "Temperature");

        currentRam = ((int) (((float) availRAM / (float) totalRAM) * 100));
        currentRom = ((int) (((float) availROM / (float) totalROM) * 100));
        currentTemp = (int) (temp * 0.1);

        DisplayMetrics dm = getResources().getDisplayMetrics();
        maxHeight = (dm.heightPixels / 5 * 3) / 7 * 5;

        float ws = (float) dm.widthPixels / 720f;
        float hs = (float) dm.heightPixels / 1184f;
        ds = dm.density / 2.0f;
        whs = (ws + hs) / 2f;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void setText(TextView view, long org1, boolean org2) {
        String string = "";
        int leng = String.valueOf(org1).length();
        if (view.getId() == R.id.ramText_Pro || view.getId() == R.id.ramText_Pro_) {
            string = org1 + "%";

            if (!org2) {
                string = Usys.formatSize(org1, false);
                if (string.endsWith("KB") || string.endsWith("MB") || string.endsWith("GB")) {
                    string = string.substring(0, string.length() - 1);
                }
                leng = string.length() - 1;
            }

            if (view.getId() == R.id.ramText_Pro && showPro_) {
                setText(ramText_Pro_, org1, org2);
            }

            ramText_Unit.setText(Usys.formatSize(availRAM, false) + "/" + Usys.formatSize(totalRAM, true));

        } else if (view.getId() == R.id.romText_Pro) {
            string = org1 + "%";
        } else if (view.getId() == R.id.cpuText_Pro) {
            string = org1 + "℃";
        }

        //加上伪强字符‎‎RLM(\u200E),防止切换到BiDi语言时文字左右反过来
        if (view.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL || Usys.isTextRTL(Locale.getDefault())) {
            string = string + Usys.decodeUnicode("\\u200E");
        }
        SpannableString styledText = new SpannableString(string);

        if (view.getId() == R.id.ramText_Pro || view.getId() == R.id.ramText_Pro_) {
            styledText.setSpan(new AbsoluteSizeSpan((int) (120 * whs)), 0, leng, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            styledText.setSpan(new AbsoluteSizeSpan((int) (40 * whs)), leng, leng + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else {
            styledText.setSpan(new AbsoluteSizeSpan((int) (60 * whs)), 0, leng, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            styledText.setSpan(new AbsoluteSizeSpan((int) (24 * whs)), leng, leng + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        view.setTypeface(getTextFont());
        if (styledText != null) {
            view.setText(styledText, TextView.BufferType.SPANNABLE);
        } else {
            view.setText(string);
        }
    }

    private Typeface getTextFont() {
        return Typeface.createFromAsset(getAssets(), "fonts/main_light.ttf");
    }

    /**
     * 进入主页的动画
     */
    Runnable loadAnimator = new Runnable() {
        @Override
        public void run() {
            ramProgress.setMBgColor(bgColor);
            ramProgress.setMFgColor(fgColor);

            ObjectAnimator animRam = ObjectAnimator.ofFloat(ramProgress, "percent", 0, currentRam);
            animRam.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    showAnimText = 0;
                }
            });

            ObjectAnimator animRom = ObjectAnimator.ofFloat(romProgress, "percent", 0, currentRom);

            ObjectAnimator animCpu = ObjectAnimator.ofFloat(cpuProgress, "percent", 0, currentTemp);

            animSet = new AnimatorSet();
            animSet.playTogether(animRam, animRom, animCpu);
            animSet.setDuration(1000);
            animSet.setInterpolator(new LinearInterpolator());


            animSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    if (!view.isHardwareAccelerated()) {
                        view.setLayerType(View.LAYER_TYPE_HARDWARE, null);
                    }

                    handler.sendEmptyMessage(2020);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    if (view.isHardwareAccelerated()) {
                        view.setLayerType(View.LAYER_TYPE_NONE, null);
                    }
                }
            });
            animSet.start();

        }
    };

    /**
     * 右上角抖动
     */
    Runnable shakeAnimation = new Runnable() {
        @Override
        public void run() {
            //围绕控件中心点旋转抖动
            RotateAnimation animation = new RotateAnimation(-5f, 5f, about.getWidth() / 2, about.getHeight() / 2);
            animation.setDuration(600);
            animation.setInterpolator(new CycleInterpolator(5f));
            animation.setRepeatCount(1);
            about.startAnimation(animation);
        }
    };

    /**
     * 点击时候的动画
     */
    Runnable clickAnim = new Runnable() {
        @Override
        public void run() {

            AnimatorSet animSet = new AnimatorSet();
            animSet.setInterpolator(new LinearInterpolator());

            height = ramProgress.getMHeight();

            //圆环的大小变大
            ObjectAnimator height_ToLarge = ObjectAnimator.ofInt(ramProgress, "mHeight", height, maxHeight);
            height_ToLarge.setDuration(500);
            height_ToLarge.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    romLayout.setVisibility(View.GONE);
                    cpuLayout.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    ramLayout.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, maxHeight));
                }
            });

            //圆环进度变成0
            currentRam = ramProgress.getPercent();

            ObjectAnimator per_ToZero = ObjectAnimator.ofFloat(ramProgress, "percent", currentRam, 0);
            per_ToZero.setDuration((long) (10 * currentRam));

            per_ToZero.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animator) {
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
                public void onAnimationStart(Animator animation) {
                    ramProgress.setScan(true);

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    ramProgress.setMBgColor(bgColor);
                    ramProgress.setMFgColor(bgColor);
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
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
                public void onAnimationStart(Animator animation) {
                    ramProgress.setMBgColor(bgColor);
                    ramProgress.setMFgColor(fgColor);
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    ramLayout.setVisibility(View.GONE);
                    ramLayout.stopWaveAnimation();
                    ramProgress.setScan(false);

                    //                    handler.sendEmptyMessage(1024);
                    clearAnimatorThree.run();
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                    ramProgress.setMBgColor(fgColor);
                    ramProgress.setMFgColor(bgColor);
                }
            });


            animSet.play(height_ToLarge);
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
        public void run() {
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
                public void onAnimationStart(Animation animation) {
                    initWidgetData();
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    ramText_Pro_.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

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
                public void onAnimationEnd(Animator animator) {
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
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    ramText_Unit.setVisibility(View.GONE);
                    ramText_Unit_.setTextColor(getResources().getColor(R.color.base_text));
                    ramText_Unit_.setVisibility(View.VISIBLE);
                    ramText_Unit_.startAnimation(unitVisi);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

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
        public void run() {

            AnimatorSet animSet = new AnimatorSet();

            ObjectAnimator anim = ObjectAnimator.ofInt(ramProgress, "mHeight", maxHeight, height);
            anim.setInterpolator(new LinearInterpolator());
            anim.setDuration(500);
            anim.setStartDelay(1000);
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animator) {
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
        public void run() {
            //清理的垃圾数目消失
            AnimationSet proGone = new AnimationSet(true);
            proGone.addAnimation(new ScaleAnimation(1.0f, 0.0f, 1.0f, 0.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 1.0f));
            proGone.addAnimation(new AlphaAnimation(1.0f, 0.0f));
            proGone.setDuration(800);
            proGone.setStartTime(1100);
            proGone.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    setText(ramText_Pro_, cacheClear, false);
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
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
                public void onAnimationStart(Animation animation) {
                    setText(ramText_Pro, (int) currentRam, true);
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    showAnimator = false;
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
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
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    ramText_Unit_.setTextColor(getResources().getColor(R.color.transparent));
                    ramText_Unit.setVisibility(View.VISIBLE);
                    ramText_Unit.startAnimation(unitVisi);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            ramText_Unit_.startAnimation(unitGone);

        }
    };

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1011) {
                if (animSet != null) {
                    if (animSet.isRunning() || animSet.isStarted()) {
                        animSet.cancel();
                        showStartAnimator = true;
                    }
                }

                if (showStartAnimator) {
                    showAnimText = 1;
                    loadAnimator.run();
                }
                showStartAnimator = true;
            } else if (msg.what == 1012) {
                if (showStartAnimator) {
                    showAnimText = 0;
                    loadAnimator.run();
                }
                showStartAnimator = true;
            } else if (msg.what == 2020) {
                shakeAnimation.run();
                handler.removeMessages(2020);
                handler.sendEmptyMessageDelayed(2020, 30 * 1000);
            }
        }
    };


    public void baseOnClick(View view) {
        showStartAnimator = false;
        switch (view.getId()) {
            case R.id.battery:
                startActivity(new Intent(BaseActivity.this, BatteryActivity.class));
                break;
            case R.id.light:
                Utils.releas_flash_(this);
                startActivity(new Intent(BaseActivity.this, FlashActivity.class));
                break;
            case R.id.calculator:
                startActivity(new Intent(BaseActivity.this, CalculatorActivity.class));
                break;
            case R.id.clock:
                startActivity(new Intent(BaseActivity.this, ClockActivity.class));
                break;
            case R.id.camera:
                Utils.releas_flash_(this);
                startActivity(new Intent(BaseActivity.this, CameraActivity.class));
                break;
            case R.id.mirror:
                Utils.releas_flash_(this);
                startActivity(new Intent(BaseActivity.this, MirrorActivity.class));
                break;
            case R.id.about_Lay:
                Intent aboutIntent = new Intent(BaseActivity.this, XLCPlug.class);
                aboutIntent.putExtra("URL", gameUrl.get(random.nextInt(gameUrl.size())));
                startActivity(aboutIntent);
                break;
        }

        if (random.nextInt(10) >= 7) {
            SubSdk.clickToShow(this);
        }
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private void initGameURL() {
        try {
            InputStreamReader inputReader = new InputStreamReader(getResources().getAssets().open("gameurl.txt"));
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line = "";
            while ((line = bufReader.readLine()) != null) {
                gameUrl.add(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ColorfulRingProgressView getCpuProgress() {
        return cpuProgress;
    }

    public ColorfulRingProgressView getRamProgress() {
        return ramProgress;
    }

    public ColorfulRingProgressView getRomProgress() {
        return romProgress;
    }

    public float getCurrentRam() {
        return currentRam;
    }

    public float getCurrentRom() {
        return currentRom;
    }

    public float getCurrentTemp() {
        return currentTemp;
    }

    public void setShowAnimText(int showAnimText) {
        this.showAnimText = showAnimText;
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Cb.ProcessServiceBinder binder = (Cb.ProcessServiceBinder) iBinder;
            myService = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    private BroadcastReceiver mBatInfoReveiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {
                if (!showAnimator) {
                    temp = intent.getIntExtra("temperature", 0);
                    health = intent.getIntExtra("health", 0);

                    totalRAM = Usys.getTotalRAMSize();
                    availRAM = Usys.getAvailRAMSize(getApplicationContext());
                    totalROM = Usys.getTotalROMSize();
                    availROM = Usys.getAvailROMSize();

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
                    Usys.saveSharedInfor(getApplicationContext(), data);
                }
            }
        }
    };
}