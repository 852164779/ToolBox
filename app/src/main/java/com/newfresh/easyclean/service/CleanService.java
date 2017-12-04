package com.newfresh.easyclean.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;

import java.util.List;

/**
 * Created by xlc on 2017/5/24.
 */

public class CleanService extends Service {

    private CleanService.OnPeocessActionListener mOnActionListener;

    private ActivityManager activityManager = null;

    public interface OnPeocessActionListener {
        void onCleanCompleted (Context context, long cacheSize);
    }

    public class ProcessServiceBinder extends Binder {
        public CleanService getService () {
            return CleanService.this;
        }
    }

    @Override
    public IBinder onBind (Intent intent) {
        return new CleanService.ProcessServiceBinder();
    }

    @Override
    public void onCreate () {
        try {
            activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    @Override
    public int onStartCommand (Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    private class TaskClean extends AsyncTask<Void, Void, Long> {
        @Override
        protected void onPreExecute () {
        }

        @Override
        protected Long doInBackground (Void... params) {
            List<ActivityManager.RunningAppProcessInfo> appList = activityManager.getRunningAppProcesses();
            List<ActivityManager.RunningServiceInfo> serviceLiser = activityManager.getRunningServices(100);

            long beforMem = getSize();

            if ( appList != null ) {
                for ( int i = 0; i < appList.size(); i++ ) {
                    ActivityManager.RunningAppProcessInfo app = appList.get(i);
                    if ( app.importance > ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE ) {
                        String[] pkgList = app.pkgList;
                        for ( int j = 0; j < pkgList.length; j++ ) {
                            activityManager.killBackgroundProcesses(pkgList[j]);
                        }
                    }
                }
            }

            if ( serviceLiser != null ) {
                for ( int i = 0; i < serviceLiser.size(); i++ ) {
                    ActivityManager.RunningServiceInfo app = serviceLiser.get(i);
                    activityManager.killBackgroundProcesses(app.process);
                }
            }

            long afterMem = getSize();

            return Math.abs(afterMem - beforMem);
        }

        @Override
        protected void onPostExecute (Long result) {
            if ( mOnActionListener != null ) {
                mOnActionListener.onCleanCompleted(CleanService.this, result);
            }
        }

        public long getSize () {
            ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
            activityManager.getMemoryInfo(memoryInfo);
            return memoryInfo.availMem;
        }
    }

    public void cleanAllProcess () {
        new TaskClean().execute();
    }

    public void setOnActionListener (CleanService.OnPeocessActionListener listener) {
        mOnActionListener = listener;
    }
}