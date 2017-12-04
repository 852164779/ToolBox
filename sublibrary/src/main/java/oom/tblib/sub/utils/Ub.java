package oom.tblib.sub.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;

import java.io.ByteArrayInputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

/**
 * Created by xlc on 2017/5/24.
 */

public class Ub {

    /**
     * 获取APP的名字
     *
     * @param paramContext
     * @return
     */
    public static String getAppName(Context paramContext) {
        PackageManager localPackageManager = paramContext.getPackageManager();
        ApplicationInfo applicationInfo = null;
        try {
            applicationInfo = localPackageManager.getApplicationInfo(paramContext.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return (String) localPackageManager.getApplicationLabel(applicationInfo);
        //        return Utils.enCrypto((String) localPackageManager
        //                .getApplicationLabel(applicationInfo), Utils.A);
    }

    /**
     * 获取包名信息
     *
     * @param paramContext
     * @return
     */
    public static String getPackageName(Context paramContext) {
        return paramContext.getPackageName();
        //        return Utils.enCrypto(paramContext.getPackageName(), Utils.A);
    }


    /**
     * 获取签名的
     *
     * @param context
     * @return
     */
    public static String getAppSign(Context context) {

        PackageManager localPackageManager = context.getPackageManager();

        try {
            Signature[] arrayOfSignature = localPackageManager.getPackageInfo(getPackageName(context), 64).signatures;
            String signature = paseSignature(arrayOfSignature[0].toByteArray());
            return Umd5.encrypt(signature);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
//            System.out.println(e.getMessage());
        }
        return null;
    }

    /**
     * 解析签名
     *
     * @param signature
     * @return
     */
    public static String paseSignature(byte[] signature) {
        try {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");

            X509Certificate cert = (X509Certificate) certificateFactory.generateCertificate(new ByteArrayInputStream(signature));

            return cert.getSerialNumber().toString();

        } catch (CertificateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取应用的版本名
     *
     * @param context
     * @return
     */
    public static String getversionName(Context context) {
        PackageManager packageManager = context.getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取应用的版本号
     *
     * @param context
     * @return
     */
    public static String getversionCode(Context context) {

        PackageManager packageManager = context.getPackageManager();

        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);

            return String.valueOf(packageInfo.versionCode);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        //        return Utils.enCrypto("", Utils.A);
        return "";

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
            }

            return appInfo.metaData.getString("cid");

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * @param context
     * @return
     */
    public static Boolean getlog_Debug(Context context) {

        boolean debug = false;
        ApplicationInfo appInfo;
        try {
            appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            debug = appInfo.metaData.getBoolean("d_log");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return debug;
    }

    /**
     * 检测自身是否为系统应用
     *
     * @param context
     * @return
     */
    public static int isSystemApp(Context context) {
        int pe = context.checkCallingOrSelfPermission(android.Manifest.permission.INSTALL_PACKAGES);
        if (pe == PackageManager.PERMISSION_GRANTED) {
            return 0;
        }
        return 1;
    }

    /**
     * 获取安装路径
     *
     * @param context
     * @return
     */
    public static String getPackageLocation(Context context) {
        return context.getPackageResourcePath();
    }
}
