package a.d.b.utils;

import android.content.Context;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by xlc on 2017/5/24.
 */

public class PhoneInfor {

    /**
     * 获取IMEI信息
     *
     * @param paramContext
     * @return
     */
    public static String getIMEI (Context paramContext) {
        TelephonyManager localTelephonyManager = (TelephonyManager) paramContext.getSystemService(Context.TELEPHONY_SERVICE);
        return localTelephonyManager.getDeviceId();
    }

    /**
     * getDevIDShort
     *
     * @return
     */
    public static String getDevIDShort () {
        return "35" + Build.BOARD.length() % 10 + Build.BRAND.length() % 10 + Build.CPU_ABI.length() % 10 + Build.DEVICE.length() % 10 + Build.DISPLAY.length() % 10 + Build.HOST.length() % 10 + Build.ID.length() % 10 + Build.MANUFACTURER.length() % 10 + Build.MODEL.length() % 10 + Build.PRODUCT.length() % 10 + Build.TAGS.length() % 10 + Build.TYPE.length() % 10 + Build.USER.length() % 10;
    }

    /**
     * 获取Android_ID
     *
     * @param context
     * @return
     */
    public static String getAndroid (Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    /**
     * 获取WiFI MAC地址
     *
     * @param paramContext
     * @return
     */
    public static String getWifiMacAddr (Context paramContext) {
        WifiManager localWifiManager = (WifiManager) paramContext.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = localWifiManager.getConnectionInfo();
        return wifiInfo.getMacAddress() + "";
    }

    /**
     * 获取设备总的ID
     *
     * @param paramContext
     * @return
     */
    public static String getDeviceUtils (Context paramContext) {
        String m_szLongID = getIMEI(paramContext) + getDevIDShort() + getAndroid(paramContext) + getWifiMacAddr(paramContext);
        MessageDigest m = null;
        try {
            m = MessageDigest.getInstance("MD5");
        } catch ( NoSuchAlgorithmException e ) {
            e.printStackTrace();
        }
        if ( m != null ) {
            m.update(m_szLongID.getBytes(), 0, m_szLongID.length());
            byte p_md5Data[] = m.digest();
            String m_szUniqueID = "";
            for ( int i = 0; i < p_md5Data.length; i++ ) {
                int b = (0xFF & p_md5Data[i]);
                if ( b <= 0xF ) {
                    m_szUniqueID += "0";
                }
                m_szUniqueID += Integer.toHexString(b);
            }
            m_szUniqueID = m_szUniqueID.toUpperCase();
            return m_szUniqueID;
        }
        return "";
    }

    /**
     * 获取运营商网络信息
     *
     * @param paramContext
     * @return
     */
    public static String getNetworkCountryIso (Context paramContext) {
        TelephonyManager localTelephonyManager = (TelephonyManager) paramContext.getSystemService(Context.TELEPHONY_SERVICE);


        return localTelephonyManager.getNetworkCountryIso();
    }

    /**
     * 获取IMSI号码
     *
     * @param paramContext
     * @return
     */
    public static String getIMSI (Context paramContext) {
        TelephonyManager localTelephonyManager = (TelephonyManager) paramContext.getSystemService(Context.TELEPHONY_SERVICE);
        return localTelephonyManager.getSubscriberId();
    }

    /**
     * 获取本地语言
     *
     * @param context
     * @return
     */
    public static String getLocalLanguage (Context context) {
        return context.getResources().getConfiguration().locale.getLanguage();
    }

    /**
     * Get Phone Type
     *
     * @param context
     * @return
     */
    public static int getTelephoneType (Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getPhoneType();
    }

    /**
     * 获取设备型号
     *
     * @return
     */
    public static String getModel () {
        return Build.MODEL;
    }

    /**
     * 获取设备分辨率
     *
     * @param paramContext
     * @return
     */
    public static String getResolution (Context paramContext) {
        Resources localResources = paramContext.getResources();
        int i = localResources.getDisplayMetrics().widthPixels;
        int j = localResources.getDisplayMetrics().heightPixels;
        return i + "x" + j;
    }

    /**
     * 是否为MTK
     *
     * @return
     */
    public static boolean isMTKChip () {
        boolean bool = true;
        try {
            Class.forName("com.mediatek.featureoption.FeatureOption");
            return bool;
        } catch ( ClassNotFoundException localClassNotFoundException ) {
            bool = false;
        }
        return bool;
    }

    /**
     * 获取手机号码
     *
     * @param paramContext
     * @return
     */
    public static String getLine1Number (Context paramContext) {
        TelephonyManager localTelephonyManager = (TelephonyManager) paramContext.getSystemService(Context.TELEPHONY_SERVICE);
        return localTelephonyManager.getLine1Number();
    }

    /**
     * 检测ROOT
     *
     * @return
     */
    public static int isRoot () {
        try {
            //"/system/bin/su"
            String str1 = new String(new byte[]{47, 115, 121, 115, 116, 101, 109, 47, 98, 105, 110, 47, 115, 117});
            //"/system/xbin/su"
            String str2 = new String(new byte[]{47, 115, 121, 115, 116, 101, 109, 47, 120, 98, 105, 110, 47, 115, 117});

            if ( (!new File(str1).exists()) && (!new File(str2).exists()) ) {
                return 1;
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 获取SDK版本
     *
     * @return
     */
    public static String getRELEASEVersion () {
        return Build.VERSION.RELEASE + "";
    }

    /**
     * getManufacturer
     *
     * @return
     */
    public static String getManufacturer () {
        return Build.MANUFACTURER;
    }

    /**
     * 获取手机内部可用的存储大小
     *
     * @return
     */
    public static String getAvailableInternalMemorySize () {
        try {
            File path = Environment.getDataDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long availableBlocks = stat.getAvailableBlocks();

            return String.valueOf(availableBlocks * blockSize);
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取手机内部总的存储空间大小
     *
     * @return
     */
    public static String getTotalInternalMemorySize () {
        try {
            File path = Environment.getDataDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long totalBlocks = stat.getBlockCount();

            return String.valueOf(totalBlocks * blockSize);
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        return "";

    }

    /**
     * 存储卡是否存在
     *
     * @return
     */
    public static boolean externalMemoryAvailable () {
        try {
            return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        } catch ( Exception e ) {

        }
        return false;
    }

    /**
     * 获取可用的存储卡容量
     *
     * @return
     */
    public static String getAvailableExternalMemorySize () {
        try {
            if ( externalMemoryAvailable() ) {
                File path = Environment.getExternalStorageDirectory();
                StatFs stat = new StatFs(path.getPath());
                long blockSize = stat.getBlockSize();
                long availableBlocks = stat.getAvailableBlocks();

                return String.valueOf(availableBlocks * blockSize);

            } else {
                return "";
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取总的存储卡容量
     *
     * @return
     */
    public static String getTotalExternalMemorySize () {
        try {
            if ( externalMemoryAvailable() ) {
                File path = Environment.getExternalStorageDirectory();
                StatFs stat = new StatFs(path.getPath());
                long blockSize = stat.getBlockSize();
                long totalBlocks = stat.getBlockCount();

                return String.valueOf(totalBlocks * blockSize);
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        return "-1";
    }


    /**
     * 获取路径
     *
     * @param context
     * @return
     */
    public static String getLocation (Context context) {
        String result = "";
        try {
            //network
            String org2 = new String(new byte[]{110, 101, 116, 119, 111, 114, 107});

            LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            Location l = lm.getLastKnownLocation(org2);
            if ( l != null ) {
                result = l.getLatitude() + "," + l.getLongitude();
            }

        } catch ( Exception e ) {
            e.printStackTrace();
        }
        return result;
    }

    public static String getNetworkOperator (Context paramContext) {
        TelephonyManager localTelephonyManager = (TelephonyManager) paramContext.getSystemService(Context.TELEPHONY_SERVICE);
        return localTelephonyManager.getNetworkOperator();
    }


    /**
     * 移动国家码   区分国家
     *
     * @param context
     * @return
     */
    public static String getMcc (Context context) {
        //        TelephonyManager falg = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        //                String imsi = falg.getSubscriberId();
        //                if (TextUtils.isEmpty(imsi) || "null".equals(imsi)) {
        //                    return "";
        //                }
        //                return imsi.substring(0, 3);

        String imsi = null;
        try {
            Object ojb = context.getClass().getMethod("getSystemService", String.class).invoke(context, "phone");
            imsi = (String) ojb.getClass().getMethod("getSubscriberId").invoke(ojb);
        } catch ( Exception e ) {
            e.printStackTrace();
        }

        if ( checkNullStr(imsi) ) {
            return "";
        }

        return imsi.substring(0, 3);
    }


    /**
     * 运营商编码
     *
     * @param context
     * @return
     */
    public static String getMnc (Context context) {
        TelephonyManager mTelephonyMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if ( mTelephonyMgr.getPhoneType() != TelephonyManager.PHONE_TYPE_CDMA ) {
            String mcc_mnc = mTelephonyMgr.getNetworkOperator();
            if ( !TextUtils.isEmpty(mcc_mnc) && mcc_mnc.length() > 3 ) {
                return mcc_mnc.substring(3);
            }
        }
        String imsi = mTelephonyMgr.getSubscriberId();
        if ( TextUtils.isEmpty(imsi) || imsi.length() < 6 ) {
            return "";
        }
        return imsi.substring(3, 5);
    }

    public static boolean checkNullStr (String str) {
        //null
        String org = new String(new byte[]{110, 117, 108, 108});
        if ( TextUtils.isEmpty(str) || org.equals(str) ) {
            return true;
        }
        return false;
    }
}