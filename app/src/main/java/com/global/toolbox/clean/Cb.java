package com.global.toolbox.clean;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by xlc on 2017/5/24.
 */

public class Cb extends Service {

    public static final String ACTION_CLEAN_AND_EXIT = "com.yzy.service.cleaner.CLEAN_AND_EXIT";

    private static final String TAG = "CleanerService";

    private Cb.OnPeocessActionListener mOnActionListener;

    ActivityManager activityManager = null;
    List<Ca> list = null;
    PackageManager packageManager = null;
    Context mContext;

    public interface OnPeocessActionListener {

        void onScanStarted (Context context);

        void onScanProgressUpdated (Context context, int current, int max);

        void onScanCompleted (Context context, List<Ca> apps);

        void onCleanStarted (Context context);

        void onCleanCompleted (Context context, long cacheSize);
    }

    public class ProcessServiceBinder extends Binder {
        public Cb getService() {
            return Cb.this;
        }
    }

    private Cb.ProcessServiceBinder mBinder = new Cb.ProcessServiceBinder();


    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        mContext = getApplicationContext();

        try {
            activityManager = (ActivityManager)
                    getSystemService(Context.ACTIVITY_SERVICE);
            packageManager = getApplicationContext()
                    .getPackageManager();
        } catch (Exception e) {

        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return START_NOT_STICKY;
    }


    public void killBackgroundProcesses(String processName) {

        String packageName = null;
        try {
            if (!processName.contains(":")) {
                packageName = processName;
            } else {
                packageName = processName.split(":")[0];
            }
           // activityManager.killBackgroundProcesses(packageName);

            Method forceStopPackage = activityManager.getClass()
                    .getDeclaredMethod("forceStopPackage", String.class);
            forceStopPackage.setAccessible(true);
            forceStopPackage.invoke(activityManager, packageName);

        } catch (Exception e) {

        }

    }


    private class TaskClean extends AsyncTask<Void, Void, Long> {

        @Override
        protected void onPreExecute() {
            if (mOnActionListener != null) {
                mOnActionListener.onCleanStarted(Cb.this);
            }
        }

        @Override
        protected Long doInBackground(Void... params) {
            long beforeMemory = 0;
            long endMemory = 0;
            ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
            activityManager.getMemoryInfo(memoryInfo);
            beforeMemory = memoryInfo.availMem;
            List<ActivityManager.RunningAppProcessInfo> appProcessList = activityManager
                    .getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo info : appProcessList) {
                killBackgroundProcesses(info.processName);
            }
            ActivityManager.MemoryInfo memoryInfo_1 = new ActivityManager.MemoryInfo();

            activityManager.getMemoryInfo(memoryInfo_1);

            endMemory = memoryInfo_1.availMem;


            return endMemory - beforeMemory;
        }

        @Override
        protected void onPostExecute(Long result) {

            if (mOnActionListener != null) {
                mOnActionListener.onCleanCompleted(Cb.this, result);
            }

        }
    }

    public void cleanAllProcess() {
        //  mIsCleaning = true;
        new TaskClean().execute();

    }

    public void setOnActionListener(Cb.OnPeocessActionListener listener) {
        mOnActionListener = listener;
    }

    public ApplicationInfo getApplicationInfo(String processName) {
        if (processName == null) {
            return null;
        }
        List<ApplicationInfo> appList = packageManager
                .getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
        for (ApplicationInfo appInfo : appList) {
            if (processName.equals(appInfo.processName)) {
                return appInfo;
            }
        }
        return null;
    }


}
