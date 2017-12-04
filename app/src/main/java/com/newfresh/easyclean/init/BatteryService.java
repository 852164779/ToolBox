package com.newfresh.easyclean.init;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import org.json.JSONObject;

/**
 * Created by hwl on 2017/10/12.
 */

public class BatteryService extends Service {

    public static final String BatteryAction = "android.service.custom.Battery";

    private int status = 0;
    private int health = 0;
    private boolean present = false;
    private int level = 0;
    private int scale = 0;
    private int icon_small = 0;
    private int plugged = 0;
    private int voltage = 0;
    private int temperature = 0;
    private String technology = "";

    private Handler handler = new Handler() {
        @Override
        public void handleMessage (Message msg) {
            super.handleMessage(msg);
            sendBroadcast();
        }
    };

    private void sendBroadcast () {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("status", status);
            jsonObject.put("health", health);
            jsonObject.put("present", present);
            jsonObject.put("level", level);
            jsonObject.put("scale", scale);
            jsonObject.put("icon_small", icon_small);
            jsonObject.put("plugged", plugged);
            jsonObject.put("voltage", voltage);
            jsonObject.put("temperature", temperature);
            jsonObject.put("technology", technology);
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        Intent batteryIntent = new Intent(BatteryAction);
        batteryIntent.putExtra("battery", jsonObject.toString());
        sendOrderedBroadcast(batteryIntent, null);

        handler.sendEmptyMessageDelayed(105, 1000);
    }

    @Override
    public IBinder onBind (Intent intent) {
        return null;
    }

    @Override
    public void onCreate () {
        super.onCreate();
        IntentFilter batteryfilter = new IntentFilter();
        batteryfilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batteryReceiver, batteryfilter);
    }

    @Override
    public int onStartCommand (Intent intent, int flags, int startId) {
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy () {
        super.onDestroy();
        this.unregisterReceiver(batteryReceiver);
    }

    // 接收电池信息更新的广播
    private BroadcastReceiver batteryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive (Context context, Intent intent) {
            if ( !intent.getAction().equals(Intent.ACTION_BATTERY_CHANGED) ) return;

            try {
                status = intent.getIntExtra("status", 0);
                health = intent.getIntExtra("health", 0);
                present = intent.getBooleanExtra("present", false);
                level = intent.getIntExtra("level", 0);
                scale = intent.getIntExtra("scale", 0);
                icon_small = intent.getIntExtra("icon-small", 0);
                plugged = intent.getIntExtra("plugged", 0);
                voltage = intent.getIntExtra("voltage", 0);
                temperature = intent.getIntExtra("temperature", 0);
                technology = intent.getStringExtra("technology");

                handler.removeMessages(105);
                sendBroadcast();

            } catch ( Exception e ) {
                e.printStackTrace();
            }
        }
    };
}
