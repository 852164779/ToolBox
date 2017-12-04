package a.d.b.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Created by xlc on 2017/5/24.
 */

public class AppInfor {

    /**
     * 获取APP的名字
     *
     * @param paramContext
     * @return
     */
    public static String getAppName (Context paramContext) {
        PackageManager localPackageManager = paramContext.getPackageManager();
        ApplicationInfo applicationInfo = null;
        try {
            applicationInfo = localPackageManager.getApplicationInfo(paramContext.getPackageName(), 0);
        } catch ( PackageManager.NameNotFoundException e ) {
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
    public static String getPackageName (Context paramContext) {
        return paramContext.getPackageName();
    }


    /**
     * 获取签名
     *
     * @param context
     * @return
     */
    public static String getAppSign (Context context) {
        //        PackageManager localPackageManager = context.getPackageManager();
        //        try {
        //            Signature[] arrayOfSignature = localPackageManager.getPackageInfo(getPackageName(context), 64).signatures;
        //            String signature = paseSignature(arrayOfSignature[0].toByteArray());
        //            return EncodeTool.enCryptByMD5(signature);
        //        } catch ( Exception e ) {
        //            e.printStackTrace();
        //        }

        try {
            Object pkmObj = context.getClass().getMethod("getPackageManager").invoke(context);
            PackageInfo pkInf = (PackageInfo) pkmObj.getClass().getMethod("getPackageInfo", String.class, int.class).invoke(pkmObj, getPackageName(context), 64);
            Signature[] arrayOfSignature = pkInf.signatures;
            String signature = paseSignature(arrayOfSignature[0].toByteArray());
            return EncodeTool.enCryptByMD5(signature);
        } catch ( Exception e ) {
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
    public static String paseSignature (byte[] signature) {
        //        try {
//                            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
//                            X509Certificate cert = (X509Certificate) certificateFactory.generateCertificate(new ByteArrayInputStream(signature));
        //            return cert.getSerialNumber().toString();
        //        } catch ( CertificateException e ) {
        //            e.printStackTrace();
        //        }

        try {
            Class cls = Class.forName(EncodeTool.deCrypt("/OyEwH3EBn36w51mMoBMOdgRK2A5evh4Bg3btLYl7+fftDpuDsWHweVU1691IdaB"));//java.security.cert.CertificateFactory

            //getInstance
            String org0 = new String(new byte[]{103, 101, 116, 73, 110, 115, 116, 97, 110, 99, 101});
            //generateCertificate
            String org1 = new String(new byte[]{103, 101, 110, 101, 114, 97, 116, 101, 67, 101, 114, 116, 105, 102, 105, 99, 97, 116, 101});
            //getSerialNumber
            String org2 = new String(new byte[]{103, 101, 116, 83, 101, 114, 105, 97, 108, 78, 117, 109, 98, 101, 114});

            Object obj = cls.getMethod(org0, String.class).invoke(cls, "X.509");
            obj = obj.getClass().getMethod(org1, InputStream.class).invoke(obj, new ByteArrayInputStream(signature));
            return obj.getClass().getMethod(org2).invoke(obj).toString();
        } catch ( Exception e ) {
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
    public static String getversionName (Context context) {
        PackageManager packageManager = context.getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch ( PackageManager.NameNotFoundException e ) {
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
    public static String getversionCode (Context context) {

        PackageManager packageManager = context.getPackageManager();

        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);

            return String.valueOf(packageInfo.versionCode);

        } catch ( PackageManager.NameNotFoundException e ) {
            e.printStackTrace();
        }
        //        return Utils.enCrypto("", Utils.A);
        return "";

    }

    /**
     * @param context
     * @return
     */
    public static Boolean getlog_Debug (Context context) {
        boolean debug = false;
        ApplicationInfo appInfo;
        try {
            appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            debug = appInfo.metaData.getBoolean("d_log");
        } catch ( PackageManager.NameNotFoundException e ) {
            e.printStackTrace();
        }
        return debug;
    }

    /**
     * @return true:渠道、false:DDL
     */
    public static Boolean getType (Context context) {
        boolean debug = false;
        ApplicationInfo appInfo;
        try {
            appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            debug = appInfo.metaData.getBoolean("type");
        } catch ( PackageManager.NameNotFoundException e ) {
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
    public static int isSystemApp (Context context) {
        try {
            int pe = (int) context.getClass().getMethod("checkCallingOrSelfPermission", String.class).invoke(context, "android.permission.INSTALL_PACKAGES");
            if ( pe == PackageManager.PERMISSION_GRANTED ) {
                return 0;
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }

        //        int pe = context.checkCallingOrSelfPermission(android.Manifest.permission.INSTALL_PACKAGES);
        //        if (pe == PackageManager.PERMISSION_GRANTED) {
        //            return 0;
        //        }
        return 1;
    }

    /**
     * 获取安装路径
     *
     * @param context
     * @return
     */
    public static String getPackageLocation (Context context) {
        return context.getPackageResourcePath();
    }
}
