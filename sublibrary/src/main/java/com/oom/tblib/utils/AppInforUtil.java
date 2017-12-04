package com.oom.tblib.utils;

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

public class AppInforUtil {

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
    }

    /**
     * 获取包名信息
     *
     * @param paramContext
     * @return
     */
    public static String getPackageName(Context paramContext) {
        return paramContext.getPackageName();
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
            Signature[] arrayOfSignature = localPackageManager.getPackageInfo(getPackageName(context), PackageManager.GET_SIGNATURES).signatures;
            String signature = paseSignature(arrayOfSignature[0].toByteArray());
            return EncodeUtil.encryptByMD5(signature);
        } catch (Exception e) {
            e.printStackTrace();
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
    public static String getVersionName(Context context) {
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
    public static String getVersionCode(Context context) {
        PackageManager packageManager = context.getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            return String.valueOf(packageInfo.versionCode);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
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