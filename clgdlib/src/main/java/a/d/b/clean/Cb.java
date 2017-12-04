package a.d.b.clean;

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

public class Cb extends Service {

    public static final String ACTION_CLEAN_AND_EXIT = "com.yzy.service.cleaner.CLEAN_AND_EXIT";

    private static final String TAG = "CleanerService";

    private OnCleanUPListener cleanUP;

    ActivityManager activityManager = null;
    List<Ca> list = null;
    PackageManager packageManager = null;
    Context mContext;

    public interface OnCleanUPListener {

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

    @Override
    public IBinder onBind(Intent intent) {
        return new ProcessServiceBinder();
    }

    @Override
    public void onCreate() {
        mContext = getApplicationContext();

        try {
            activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            packageManager = getApplicationContext().getPackageManager();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    public void cleanAllProcess() {
        new CLeanTask().execute();
    }

    public void setOnActionListener(OnCleanUPListener listener) {
        this.cleanUP = listener;
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

    class CLeanTask extends AsyncTask<Void, Void, Long> {
        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Long doInBackground(Void... params) {

            List<ActivityManager.RunningAppProcessInfo> appList = activityManager.getRunningAppProcesses();
            List<ActivityManager.RunningServiceInfo> serviceLiser = activityManager.getRunningServices(100);

            //                        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
            //                        activityManager.getMemoryInfo(memoryInfo);
            //                        long beforMem = memoryInfo.availMem;

            long beforMem = getSize();


            if (appList != null) {
                for (int i = 0; i < appList.size(); i++) {
                    ActivityManager.RunningAppProcessInfo app = appList.get(i);
                    if (app.importance > ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE) {
                        String[] pkgList = app.pkgList;
                        for (int j = 0; j < pkgList.length; j++) {
                            activityManager.killBackgroundProcesses(pkgList[j]);
                        }
                    }
                }
            }

            if (serviceLiser != null) {
                for (int i = 0; i < serviceLiser.size(); i++) {
                    ActivityManager.RunningServiceInfo app = serviceLiser.get(i);
                    activityManager.killBackgroundProcesses(app.process);
                }
            }
            //
            //                        memoryInfo = new ActivityManager.MemoryInfo();
            //                        activityManager.getMemoryInfo(memoryInfo);
            //                        long afterMem = memoryInfo.availMem;

            long afterMem = getSize();

            return Math.abs(afterMem - beforMem);
        }

        @Override
        protected void onPostExecute(Long result) {
            if (cleanUP != null) {
                cleanUP.onCleanCompleted(Cb.this, result);
            }
        }

        public long getSize() {
            try {
                //          ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
                Class cls = Class.forName("android.app.ActivityManager$MemoryInfo");
                ActivityManager.MemoryInfo obj = (ActivityManager.MemoryInfo) cls.newInstance();

                activityManager.getClass().getMethod("getMemoryInfo", obj.getClass()).invoke(activityManager, obj);
                //         activityManager.getMemoryInfo(memoryInfo);


                return obj.availMem;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return 0l;
        }
    }
}
