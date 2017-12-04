package com.xxm.toolbox.clean;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;

import java.util.List;

/**
 * Created by xlc on 2017/5/24.
 */
public class CleanService extends Service {

    private OnPeocessActionListener mOnActionListener;

    private ActivityManager activityManager = null;
    private List<CleanComar> list = null;
    private PackageManager packageManager = null;
    private Context context;

    interface OnPeocessActionListener {

        void onScanStarted (Context context);

        void onScanProgressUpdated (Context context, int current, int max);

        void onScanCompleted (Context context, List<CleanComar> apps);

        void onCleanStarted (Context context);

        void onCleanCompleted (Context context, long cacheSize);
    }

    public class ProcessServiceBinder extends Binder {
        public CleanService getService() {
            return CleanService.this;
        }
    }

    private CleanService.ProcessServiceBinder mBinder = new CleanService.ProcessServiceBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        context = getApplicationContext();
        try {
            activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            packageManager = context.getPackageManager();
        } catch (Exception e) {

        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    private class TaskClean extends AsyncTask<Void, Void, Long> {
        @Override
        protected Long doInBackground(Void... params) {
            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> appList = am.getRunningAppProcesses();
            List<ActivityManager.RunningServiceInfo> serviceLiser = am.getRunningServices(100);

            ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
            activityManager.getMemoryInfo(memoryInfo);

            long beforMem = memoryInfo.availMem;

            if (appList != null) {
                for (int i = 0; i < appList.size(); i++) {
                    ActivityManager.RunningAppProcessInfo app = appList.get(i);
                    if (app.importance > ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE) {
                        String[] pkgList = app.pkgList;
                        for (int j = 0; j < pkgList.length; j++) {
                            am.killBackgroundProcesses(pkgList[j]);
                        }
                    }
                }
            }

            if (serviceLiser != null) {
                for (int i = 0; i < serviceLiser.size(); i++) {
                    ActivityManager.RunningServiceInfo app = serviceLiser.get(i);
                    am.killBackgroundProcesses(app.process);
                }
            }

            memoryInfo = new ActivityManager.MemoryInfo();
            activityManager.getMemoryInfo(memoryInfo);
            long afterMem = memoryInfo.availMem;

            return Math.abs(afterMem - beforMem);
        }

        @Override
        protected void onPostExecute(Long result) {
            if (mOnActionListener != null) {
                mOnActionListener.onCleanCompleted(CleanService.this, result);
            }
        }
    }

    public void cleanAllProcess() {
        new TaskClean().execute();
    }

    public void setOnActionListener(CleanService.OnPeocessActionListener listener) {
        mOnActionListener = listener;
    }

    public ApplicationInfo getApplicationInfo(String processName) {
        if (processName == null) {
            return null;
        }
        List<ApplicationInfo> appList = packageManager.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
        for (ApplicationInfo appInfo : appList) {
            if (processName.equals(appInfo.processName)) {
                return appInfo;
            }
        }
        return null;
    }
}