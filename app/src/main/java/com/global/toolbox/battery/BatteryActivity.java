package com.global.toolbox.battery;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.global.toolbox.R;
import com.global.toolbox.util.SleepAdapter;
import com.global.toolbox.util.Utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;


/**
 * Created by xlc on 2016/11/25.
 */
public class BatteryActivity extends Activity implements View.OnClickListener {


    private ImageView imageView;

    private FrameLayout layout;

    private TextView battery_current;

    private TextView wendu, dianya, jiangkan, jishu;

    private ImageView dianchi, chatou, usb;

    private ImageView wendu_img, dianya_img;

    private ImageView battery_img;

    private DecimalFormat df = new DecimalFormat("###.0");


    private ImageView wifiImag, gpsImage, moblieImage, screenImage, blueImge;

    private RelativeLayout battery_seek_layout;

    private LinearLayout battery_sleep_time;

    private SeekBar screen_light_seek;

    private WifiManager wifiManager = null;

    private boolean gps_status = false;

    private boolean wifi_status = false;

    private boolean screen_brightness = false;

    private boolean moblie_net = false;

    private int intScreenBrightness;

    private BluetoothAdapter bluetoothAdapter;

    private boolean bluetooth = false;

    private Dialog sleepDialog;

    private final int[] src = {30 * 1000, 60 * 1000, 2 * 60 * 1000, 10 * 60 * 1000, 30 * 60 * 1000};

    private ListView listView;

    private SleepAdapter adapter;

    private TextView sleep_text;


    @SuppressLint ("WifiManagerLeak")
    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.battery_activity);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        initView();

        registerReceiver(mBatInfoReveiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

    }

    @Override
    public void onDestroy () {
        super.onDestroy();

        unregisterReceiver(mBatInfoReveiver);
    }


    private void initView () {
        battery_img = (ImageView) findViewById(R.id.battery_img);
        wendu_img = (ImageView) findViewById(R.id.wendu_img);
        dianya_img = (ImageView) findViewById(R.id.dianya_img);
        wendu = (TextView) findViewById(R.id.wendu);
        dianya = (TextView) findViewById(R.id.dianya);
        jiangkan = (TextView) findViewById(R.id.jiankang);
        jishu = (TextView) findViewById(R.id.jishu);
        dianchi = (ImageView) findViewById(R.id.dianchi);
        chatou = (ImageView) findViewById(R.id.chatou);
        usb = (ImageView) findViewById(R.id.usb);
        layout = (FrameLayout) findViewById(R.id.layout);
        imageView = (ImageView) findViewById(R.id.battery_change);
        battery_current = (TextView) findViewById(R.id.battery_current);


        wifiImag = (ImageView) findViewById(R.id.battery_wifi);
        gpsImage = (ImageView) findViewById(R.id.battery_gps);
        moblieImage = (ImageView) findViewById(R.id.battery_2g);
        screenImage = (ImageView) findViewById(R.id.battery_screen_light);
        blueImge = (ImageView) findViewById(R.id.battery_blue);
        wifiImag.setOnClickListener(this);
        gpsImage.setOnClickListener(this);
        moblieImage.setOnClickListener(this);
        screenImage.setOnClickListener(this);
        blueImge.setOnClickListener(this);
        battery_sleep_time = (LinearLayout) findViewById(R.id.battery_sleep_time);

        battery_sleep_time.setOnClickListener(this);
        battery_seek_layout = (RelativeLayout) findViewById(R.id.battery_seek_layout);

        sleep_text = (TextView) findViewById(R.id.battery_sleep_text);

        set_sleep_text();

        screen_light_seek = (SeekBar) findViewById(R.id.battery_seek);

        screen_light_seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged (SeekBar seekBar, int progress, boolean fromUser) {

                setScreenBritness(progress);
            }

            @Override
            public void onStartTrackingTouch (SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch (SeekBar seekBar) {

            }
        });
        if ( wifiManager.getWifiState() == 3 ) {

            wifiImag.setImageResource(R.drawable.battery_on);

            wifi_status = true;

        } else {
            wifiImag.setImageResource(R.drawable.battery_off);
            wifi_status = false;
        }
        try {
            if ( Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE) == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC ) {
                battery_seek_layout.setVisibility(View.GONE);
                screen_brightness = true;
                screenImage.setImageResource(R.drawable.battery_on);

            } else {
                battery_seek_layout.setVisibility(View.VISIBLE);
                screen_brightness = false;
                screenImage.setImageResource(R.drawable.battery_off);
                screenBrightness_check();

            }
        } catch ( Settings.SettingNotFoundException e ) {
            e.printStackTrace();
        }

        if ( getMobileDataState(null) ) {
            moblieImage.setImageResource(R.drawable.battery_on);
            moblie_net = true;

        } else {
            moblieImage.setImageResource(R.drawable.battery_off);
            moblie_net = false;
        }
        if ( bluetoothAdapter != null ) {
            if ( bluetoothAdapter.isEnabled() ) {
                blueImge.setImageResource(R.drawable.battery_on);
                bluetooth = true;
            } else {
                blueImge.setImageResource(R.drawable.battery_off);
                bluetooth = false;
            }
        }

    }

    @Override
    public void onResume () {
        super.onResume();
        if ( isGPSEnable(getApplicationContext()) ) {
            gps_status = true;
            gpsImage.setImageResource(R.drawable.battery_on);
        } else {
            gps_status = false;
            gpsImage.setImageResource(R.drawable.battery_off);
        }

    }

    private void set_sleep_text () {
        int time = Utils.get_sleep_time(getApplicationContext());
        if ( time > 60000 ) {
            sleep_text.setText(time / 60000 + " minutes");
        } else if ( time == 60000 ) {
            sleep_text.setText("60 seconds");
        } else {
            sleep_text.setText("30 seconds");
        }
        setScreenOffTime(time);
    }


    private void setScreenOffTime (int paramInt) {
        try {
            Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, paramInt);
        } catch ( Exception localException ) {
            localException.printStackTrace();
        }
    }


    private void openscreenBrightness () {
        try {
            Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
            Uri uri = Settings.System.getUriFor("screen_brightness");
            getContentResolver().notifyChange(uri, null);

        } catch ( Exception e ) {
            e.printStackTrace();
        }

    }

    private void screenBrightness_check () {
        //先关闭系统的亮度自动调节
        try {
            if ( android.provider.Settings.System.getInt(getContentResolver(), android.provider.Settings.System.SCREEN_BRIGHTNESS_MODE) == android.provider.Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC ) {
                android.provider.Settings.System.putInt(getContentResolver(), android.provider.Settings.System.SCREEN_BRIGHTNESS_MODE, android.provider.Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
            }
        } catch ( Settings.SettingNotFoundException e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //获取当前亮度,获取失败则返回255
        intScreenBrightness = (int) (android.provider.Settings.System.getInt(getContentResolver(), android.provider.Settings.System.SCREEN_BRIGHTNESS, 255));
        //文本、进度条显示
        screen_light_seek.setProgress(intScreenBrightness);

    }

    /***
     * 设置亮度
     *
     * @param brightness
     */
    private void setScreenBritness (int brightness) {
        //不让屏幕全暗
        if ( brightness <= 5 ) {
            brightness = 5;
        }
        //设置当前activity的屏幕亮度
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        //0到1,调整亮度暗到全亮
        lp.screenBrightness = Float.valueOf(brightness / 255f);
        getWindow().setAttributes(lp);
        //保存为系统亮度方法1
        android.provider.Settings.System.putInt(getContentResolver(), android.provider.Settings.System.SCREEN_BRIGHTNESS, brightness);
    }


    /***
     * 判断移动网络是否开启
     *
     * @param arg
     * @return
     */
    private boolean getMobileDataState (Object[] arg) {
        try {

            ConnectivityManager mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

            Class ownerClass = mConnectivityManager.getClass();

            Class[] argsClass = null;
            if ( arg != null ) {
                argsClass = new Class[1];
                argsClass[0] = arg.getClass();
            }

            Method method = ownerClass.getMethod("getMobileDataEnabled", argsClass);

            Boolean isOpen = (Boolean) method.invoke(mConnectivityManager, arg);

            return isOpen;

        } catch ( Exception e ) {
            e.printStackTrace();
        }
        return false;
    }

    /***
     * 设置移动网络的开关
     *
     * @param enabled
     */
    public void setMobileDataStatus (boolean enabled) {
        ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        //ConnectivityManager类
        Class<?> conMgrClass = null;
        //ConnectivityManager类中的字段
        Field iConMgrField = null;
        //IConnectivityManager类的引用
        Object iConMgr = null;
        //IConnectivityManager类
        Class<?> iConMgrClass = null;
        //setMobileDataEnabled方法
        Method setMobileDataEnabledMethod = null;
        try {
            //取得ConnectivityManager类
            conMgrClass = Class.forName(conMgr.getClass().getName());
            //取得ConnectivityManager类中的对象Mservice
            iConMgrField = conMgrClass.getDeclaredField("mService");
            //设置mService可访问
            iConMgrField.setAccessible(true);
            //取得mService的实例化类IConnectivityManager
            iConMgr = iConMgrField.get(conMgr);
            //取得IConnectivityManager类
            iConMgrClass = Class.forName(iConMgr.getClass().getName());

            //取得IConnectivityManager类中的setMobileDataEnabled(boolean)方法
            setMobileDataEnabledMethod = iConMgrClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);

            //设置setMobileDataEnabled方法是否可访问
            setMobileDataEnabledMethod.setAccessible(true);
            //调用setMobileDataEnabled方法
            setMobileDataEnabledMethod.invoke(iConMgr, enabled);

        } catch ( ClassNotFoundException | NoSuchMethodException | NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException | InvocationTargetException e ) {

            e.printStackTrace();
        }
    }


    private void buiderDialog () {

        if ( sleepDialog == null )

        {
            sleepDialog = new Dialog(BatteryActivity.this, R.style.progress_dialog);
        }

        sleepDialog.setCanceledOnTouchOutside(false);

        View view = getLayoutInflater().inflate(R.layout.check_sleep_time_layout, null);

        sleepDialog.setContentView(view);

        LinearLayout sleep_cancel = (LinearLayout) view.findViewById(R.id.sleep_cancel);

        sleep_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {

                sleepDialog.dismiss();

            }
        });

        listView = (ListView) view.findViewById(R.id.sleep_listView);

        adapter = new SleepAdapter(getApplicationContext(), src);

        listView.setAdapter(adapter);

        sleepDialog.getWindow().setBackgroundDrawableResource(

                android.R.color.transparent);

        if ( !isFinishing() ) {
            sleepDialog.show();
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick (AdapterView<?> parent, View view, int position, long id) {

                Utils.save_sleep_time(getApplicationContext(), src[position]);

                adapter.notifyDataSetChanged();

                if ( src[position] > 60000 ) {
                    sleep_text.setText(src[position] / 60000 + " minutes");
                } else if ( src[position] == 60000 ) {
                    sleep_text.setText("60 seconds");
                } else {
                    sleep_text.setText("30 seconds");
                }
                setScreenOffTime(src[position]);

                sleepDialog.dismiss();

            }
        });

    }


    private void to_setGPS () {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


    private boolean isGPSEnable (Context context) {
        String str = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if ( str != null ) {
            return str.contains("gps");
        } else {
            return false;
        }
    }

    @Override
    public void onClick (View v) {
        switch ( v.getId() ) {
            case R.id.battery_wifi:
                if ( wifi_status ) {
                    wifiImag.setImageResource(R.drawable.battery_off);
                    wifiManager.setWifiEnabled(false);
                    wifi_status = false;
                } else {
                    wifiImag.setImageResource(R.drawable.battery_on);
                    wifiManager.setWifiEnabled(true);
                    wifi_status = true;
                }
                break;

            case R.id.battery_gps:

                //                if (gps_status) {
                //                    gps_status = false;
                //                    gpsImage.setImageResource(R.drawable.battery_off);
                //                } else {
                //                    gps_status = true;
                //                    gpsImage.setImageResource(R.drawable.battery_on);
                //                }
                //                //toggleGPS();

                to_setGPS();

                break;

            case R.id.battery_2g:

                if ( moblie_net ) {
                    moblie_net = false;
                    moblieImage.setImageResource(R.drawable.battery_off);
                    setMobileDataStatus(false);
                } else {
                    moblie_net = true;
                    moblieImage.setImageResource(R.drawable.battery_on);
                    setMobileDataStatus(true);
                }

                break;

            case R.id.battery_screen_light:
                if ( screen_brightness ) {
                    screen_brightness = false;
                    screenBrightness_check();
                    battery_seek_layout.setVisibility(View.VISIBLE);
                    screenImage.setImageResource(R.drawable.battery_off);

                } else {
                    battery_seek_layout.setVisibility(View.GONE);
                    openscreenBrightness();
                    screenImage.setImageResource(R.drawable.battery_on);
                    screen_brightness = true;
                }
                break;

            case R.id.battery_blue:

                if ( bluetoothAdapter == null ) {
                    return;
                }
                if ( bluetooth ) {
                    blueImge.setImageResource(R.drawable.battery_off);
                    bluetooth = false;
                    bluetoothAdapter.disable();
                } else {
                    blueImge.setImageResource(R.drawable.battery_on);
                    bluetooth = true;
                    bluetoothAdapter.enable();
                }
                break;

            case R.id.battery_sleep_time:

                buiderDialog();

                break;


        }
    }


    private boolean start = true;

    private static final String CHARGER_CURRENT_NOW = "/sys/class/power_supply/battery/BatteryAverageCurrent";

    private BroadcastReceiver mBatInfoReveiver = new BroadcastReceiver() {
        int intLevel;
        int intScale;
        //温度
        int temperature;
        //电压
        int voltage;
        //健康
        int health;

        String technology;

        @Override
        public void onReceive (Context context, Intent intent) {
            // TODO Auto-generated method stub
            String action = intent.getAction();
            if ( intent.ACTION_BATTERY_CHANGED.equals(action) ) {
                intLevel = intent.getIntExtra("level", 0);
                intScale = intent.getIntExtra("scale", 0);
                temperature = intent.getIntExtra("temperature", 0);
                voltage = intent.getIntExtra("voltage", 0);
                health = intent.getIntExtra("health", 0);
                technology = intent.getStringExtra("technology");

                if ( intLevel < 60 && intLevel > 25 ) {
                    battery_img.setBackgroundResource(R.drawable.battery_yellow);
                } else if ( intLevel >= 60 ) {
                    battery_img.setBackgroundResource(R.drawable.battery_green);
                } else {
                    battery_img.setBackgroundResource(R.drawable.battery_red);
                }


                switch ( health ) {
                    case BatteryManager.BATTERY_HEALTH_GOOD:
                        jiangkan.setText(getResources().getString(R.string.good));
                        break;
                    case BatteryManager.BATTERY_HEALTH_COLD:
                        jiangkan.setText(getResources().getString(R.string.low_temperature));
                        break;
                    case BatteryManager.BATTERY_HEALTH_OVERHEAT:
                        jiangkan.setText(getResources().getString(R.string.high_temperature));
                        break;
                    case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
                        jiangkan.setText(getResources().getString(R.string.high_voltage));
                        break;
                    default:
                        jiangkan.setText(getResources().getString(R.string.good));
                        break;
                }
                int plugged = intent.getIntExtra("plugged", 0);

                switch ( plugged ) {
                    case BatteryManager.BATTERY_PLUGGED_AC:
                        chatou.setBackgroundResource(R.drawable.msg_c_charging2);
                        usb.setBackgroundResource(R.drawable.msg_usb_charging1);
                        dianchi.setBackgroundResource(R.drawable.msg_chong);
                        break;
                    case BatteryManager.BATTERY_PLUGGED_USB:
                        chatou.setBackgroundResource(R.drawable.msg_c_charging1);
                        usb.setBackgroundResource(R.drawable.msg_usb_charging2);
                        dianchi.setBackgroundResource(R.drawable.msg_chong);
                        break;
                }

                if ( temperature / 10 > 48 && temperature / 10 < 70 ) {
                    wendu_img.setBackgroundResource(R.drawable.msg_stats_indicator_yellow);
                } else if ( temperature / 10 > 70 ) {
                    wendu_img.setBackgroundResource(R.drawable.msg_stats_indicator_red);
                } else {
                    wendu_img.setBackgroundResource(R.drawable.msg_stats_indicator_green);
                }

                if ( voltage > 4.2 * 1000 ) {
                    dianya_img.setBackgroundResource(R.drawable.msg_stats_indicator_red);
                } else {
                    dianya_img.setBackgroundResource(R.drawable.msg_stats_indicator_green);
                }
                wendu.setText(df.format((float) temperature / (float) 10) + "°C");
                jishu.setText(technology);

                dianya.setText(df.format((float) voltage / (float) 1000) + "V/" + getCurrent() + "mA");

                int battery = intLevel * 100 / (intScale == 0 ? 1 : intScale);
                battery_current.setText(battery + "%");
                final int num = (dip2px(200) * battery) / 100;
                if ( start ) {
                    start = false;
                    new Thread(new Runnable() {
                        @Override
                        public void run () {
                            for ( int i = 0; i < num; i++ ) {
                                try {
                                    Thread.sleep(5);
                                } catch ( InterruptedException e ) {
                                    e.printStackTrace();
                                }
                                handler.sendEmptyMessage(i);
                            }
                        }
                    }).start();
                } else {
                    handler.sendEmptyMessage(num);
                }

                int status = intent.getIntExtra("status", BatteryManager.BATTERY_STATUS_UNKNOWN);

                if ( status == BatteryManager.BATTERY_STATUS_CHARGING ) {
                    imageView.setVisibility(View.VISIBLE);
                } else {
                    chatou.setBackgroundResource(R.drawable.msg_c_charging1);
                    usb.setBackgroundResource(R.drawable.msg_usb_charging1);
                    dianchi.setBackgroundResource(R.drawable.msg_fang);

                    imageView.setVisibility(View.GONE);
                }


            }
        }
    };

    public int dip2px (float dpValue) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage (Message msg) {
            super.handleMessage(msg);

            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(msg.what, FrameLayout.LayoutParams.WRAP_CONTENT);

            layout.setLayoutParams(layoutParams);

        }
    };

    private int getCurrent () {
        int result = 0;
        try {
            Class systemProperties = Class.forName("android.os.SystemProperties");
            Method get = systemProperties.getDeclaredMethod("get", String.class);
            String platName = (String) get.invoke(null, "ro.hardware");
            if ( platName.startsWith("mt") || platName.startsWith("MT") ) {
                String filePath = "/sys/class/power_supply/battery/device/FG_Battery_CurrentConsumption";
                // MTK平台该值不区分充放电，都为负数，要想实现充放电电流增加广播监听充电状态即可
                result = Math.round(getMeanCurrentVal(filePath, 5, 0) / 10.0f);
            } else if ( platName.startsWith("qcom") ) {
                String filePath = "/sys/class/power_supply/battery/current_now";
                int current = Math.round(getMeanCurrentVal(filePath, 5, 0) / 10.0f);
                // 高通平台该值小于0时电池处于放电状态，大于0时处于充电状态
                if ( current < 0 ) {
                    result = (-current);
                } else {
                    result = current;
                }
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        return result;
    }


    /**
     * 获取平均电流值
     * 获取 filePath 文件 totalCount 次数的平均值，每次采样间隔 intervalMs 时间
     */
    private float getMeanCurrentVal (String filePath, int totalCount, int intervalMs) {
        float meanVal = 0.0f;
        if ( totalCount <= 0 ) {
            return 0.0f;
        }
        for ( int i = 0; i < totalCount; i++ ) {
            try {
                float f = (float) readFile(filePath, 0);
                meanVal += f / totalCount;
            } catch ( Exception e ) {
                e.printStackTrace();
            }
            if ( intervalMs <= 0 ) {
                continue;
            }
            try {
                Thread.sleep(intervalMs);
            } catch ( Exception e ) {
                e.printStackTrace();
            }
        }
        return meanVal;
    }

    private int readFile (String path, int defaultValue) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(path));
            int i = Integer.parseInt(bufferedReader.readLine(), 10);
            bufferedReader.close();
            return i;
        } catch ( Exception localException ) {
        }
        return defaultValue;
    }


}


