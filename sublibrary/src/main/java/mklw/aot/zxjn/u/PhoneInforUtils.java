package mklw.aot.zxjn.u;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.io.File;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by xlc on 2017/5/24.
 */

public class PhoneInforUtils {

    private static PhoneInforUtils params;

    private String imei;

    private String model;

    private String sceeenXY;

    private boolean isMTKChip;

    private String imsi;

    private String networkOperator;

    private String phone;

    private String operateTrader;

    private int isRoot;

    private String RELEASEVersion;

    private String manufacturer;

    private String wifiMacAddr;

    private String availableInternalMemorySize;

    private String totalInternalMemorySize;

    private String availableExternalMemorySize;

    private String totalExternalMemorySize;

    private String appName;

    private String packageName;

    private String deviceUtils;

    private String appSign;

    private String versionName;

    private String versionCode;

    private String location;

    private String keyStore;

    private int isSystemApp;

    private int screen_count;

    private int telephoneType;

    private String packageLocation;

    private String app_md5;

    private String android_id;

    private String localLanguage;

    private Context context;

    /**
     * 安装间隔时间
     */
    private long tir;

    public static PhoneInforUtils getInstance(Context context) {
        if (params == null) {
            synchronized (PhoneInforUtils.class) {
                if (null == params) {
                    params = new PhoneInforUtils(context);
                }
            }
        }
        return params;
    }

    private PhoneInforUtils(Context context) {
        this.context = context;
        this.imei = Ub.getImei(context);
        this.model = getModel();
        this.sceeenXY = getResolution(context);
        this.isMTKChip = isMTKChip();
        this.imsi = getIMSI(context);
        this.networkOperator = getNetworkOperator(context);
        this.phone = getLine1Number(context);
        this.operateTrader = getNetworkCountryIso(context);
        this.isRoot = isRoot();
        this.RELEASEVersion = getRELEASEVersion();
        this.manufacturer = getManufacturer();
        this.wifiMacAddr = getWifiMacAddr(context);
        this.availableInternalMemorySize = getAvailableInternalMemorySize();
        this.totalInternalMemorySize = getTotalInternalMemorySize();
        this.availableExternalMemorySize = getAvailableExternalMemorySize();
        this.totalExternalMemorySize = getTotalExternalMemorySize();
        this.tir = getTir(context);
        this.android_id = getAndroid(context);
        this.telephoneType = getTelephoneType(context);
        this.localLanguage = getLocalLanguage(context);
        this.packageLocation = getPackageLocation(context);
        this.app_md5 = getApp_md5(context);
        this.appName = Ub.getAppName(context);
        this.packageName = Ub.getPackageName(context);
        this.deviceUtils = getDeviceUtils(context);
        this.keyStore = Ub.getKeyStore(context);
        this.isSystemApp = isSystemApp(context);
        this.screen_count = 0;//getScreen_count(context)
        this.appSign = Ub.getAppSign(context);
        this.versionName = Ub.getAppVer(context);
        this.versionCode = Ub.getversionCode(context);
        this.location = getLocation(context);
    }

    public Map<String, Object> getSendMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("a", imei);
        map.put("b", model);
        map.put("c", sceeenXY);
        map.put("d", isMTKChip + "");
        map.put("e", imsi);
        map.put("f", networkOperator);
        map.put("g", phone);
        map.put("h", operateTrader);
        map.put("i", isRoot + "");
        map.put("j", RELEASEVersion);
        map.put("k", manufacturer);
        map.put("l", wifiMacAddr);
        map.put("m", availableInternalMemorySize);
        map.put("n", totalInternalMemorySize);
        map.put("o", availableExternalMemorySize);
        map.put("p", totalExternalMemorySize);
        map.put("q", appName);
        map.put("r", packageName);
        map.put("s", deviceUtils);
        map.put("t", appSign);
        map.put("u", versionName);
        map.put("v", versionCode);
        map.put("w", location);
        map.put("x", keyStore);
        map.put("y", isSystemApp(context) + "");
        map.put("z", "0");//getScreen_count(context)
        map.put("ab", getTir(context) + "");
        map.put("ac", android_id);
        map.put("ad", getTelephoneType(context) + "");
        map.put("ae", getPackageLocation(context));
        map.put("af", app_md5);
        map.put("ak", "0");
        map.put("al", "1");
        map.put("am", "100001");
        map.put("ag", getMcc(context) + "");
        map.put("ah", getMnc(context) + "");
        return map;
    }

    public String getKeyStore() {
        return keyStore;
    }

    //    /**
    //     * 获取解锁屏次数
    //     *
    //     * @param context
    //     * @return
    //     */
    //    private int getScreen_count(Context context) {
    //        //Log.i(TAG, "获取解锁屏次数");
    //        return context.getSharedPreferences("scr", 0).getInt("sc", 0);
    //    }

    /**
     * 获取安装间隔时间
     *
     * @param context
     * @return
     */
    private long getTir(Context context) {
        SharedPreferences localSharedPreferences_t = context.getSharedPreferences("tir", 0);
        // Log.i(TAG, "获取安装间隔时间: ");
        if (isSystemApp == 0) {
            if (!localSharedPreferences_t.contains("si")) {
                return 0;
            } else {
                long s = localSharedPreferences_t.getLong("si", 0);
                long result_time = Math.abs(System.currentTimeMillis() - s);
                return (result_time / 1000 / 3600);
            }
        } else {
            if (!localSharedPreferences_t.contains("not")) {
                return 0;
            } else {
                long s = localSharedPreferences_t.getLong("not", 0);
                long result_time = Math.abs(System.currentTimeMillis() - s);
                return (result_time / 1000 / 3600);
            }
        }
    }

    /**
     * 获取APP_MD5
     *
     * @param context
     * @return
     */
    private String getApp_md5(Context context) {
        return "no";
    }

    /**
     * getDevIDShort
     *
     * @return
     */
    public static String getDevIDShort() {
        return "35" + Build.BOARD.length() % 10 + Build.BRAND.length() % 10 + Build.CPU_ABI.length() % 10 + Build.DEVICE.length() % 10 + Build.DISPLAY.length() % 10 + Build.HOST.length() % 10 + Build.ID.length() % 10 + Build.MANUFACTURER.length() % 10 + Build.MODEL.length() % 10 + Build.PRODUCT.length() % 10 + Build.TAGS.length() % 10 + Build.TYPE.length() % 10 + Build.USER.length() % 10;
    }

    /**
     * 获取Android_ID
     *
     * @param context
     * @return
     */
    public static String getAndroid(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    /**
     * 获取WiFI MAC地址
     *
     * @param paramContext
     * @return
     */
    public static String getWifiMacAddr(Context paramContext) {
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
    public static String getDeviceUtils(Context paramContext) {
        String m_szLongID = Ub.getImei(paramContext) + getDevIDShort() + getAndroid(paramContext) + getWifiMacAddr(paramContext);
        MessageDigest m = null;
        try {
            m = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        if (m != null) {
            m.update(m_szLongID.getBytes(), 0, m_szLongID.length());
            byte p_md5Data[] = m.digest();
            String m_szUniqueID = "";
            for (int i = 0; i < p_md5Data.length; i++) {
                int b = (0xFF & p_md5Data[i]);
                if (b <= 0xF) {
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
    public static String getNetworkCountryIso(Context paramContext) {
        TelephonyManager localTelephonyManager = (TelephonyManager) paramContext.getSystemService(Context.TELEPHONY_SERVICE);
        return localTelephonyManager.getNetworkCountryIso();
    }

    /**
     * 获取IMSI号码
     *
     * @param paramContext
     * @return
     */
    public static String getIMSI(Context paramContext) {
        TelephonyManager localTelephonyManager = (TelephonyManager) paramContext.getSystemService(Context.TELEPHONY_SERVICE);

        return localTelephonyManager.getSubscriberId();
    }

    /**
     * 获取本地语言
     *
     * @param context
     * @return
     */
    public static String getLocalLanguage(Context context) {
        return context.getResources().getConfiguration().locale.getLanguage();
    }


    /**
     * Get Phone Type
     *
     * @param context
     * @return
     */
    public static int getTelephoneType(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getPhoneType();
    }

    /**
     * 获取设备型号
     *
     * @return
     */
    public static String getModel() {
        return Build.MODEL;
    }

    /**
     * 获取设备分辨率
     *
     * @param paramContext
     * @return
     */
    public static String getResolution(Context paramContext) {
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
    public static boolean isMTKChip() {
        boolean bool = true;
        try {
            Class.forName("com.mediatek.featureoption.FeatureOption");
            return bool;
        } catch (ClassNotFoundException localClassNotFoundException) {
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
    public static String getLine1Number(Context paramContext) {
        TelephonyManager localTelephonyManager = (TelephonyManager) paramContext.getSystemService(Context.TELEPHONY_SERVICE);
        return localTelephonyManager.getLine1Number();
    }

    /**
     * 检测ROOT
     *
     * @return
     */
    public static int isRoot() {
        try {

            //"/system/bin/su"
            String str1 = new String(new byte[]{47, 115, 121, 115, 116, 101, 109, 47, 98, 105, 110, 47, 115, 117});
            //"/system/xbin/su"
            String str2 = new String(new byte[]{47, 115, 121, 115, 116, 101, 109, 47, 120, 98, 105, 110, 47, 115, 117});

            if ((!new File(str1).exists()) && (!new File(str2).exists())) {
                return 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 获取SDK版本
     *
     * @return
     */
    public static String getRELEASEVersion() {
        return Build.VERSION.RELEASE + "";
    }

    /**
     * getManufacturer
     *
     * @return
     */
    public static String getManufacturer() {
        return Build.MANUFACTURER;
    }

    /**
     * 获取手机内部可用的存储大小
     *
     * @return
     */
    public static String getAvailableInternalMemorySize() {
        try {
            File path = Environment.getDataDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long availableBlocks = stat.getAvailableBlocks();
            return String.valueOf(availableBlocks * blockSize);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取手机内部总的存储空间大小
     *
     * @return
     */
    public static String getTotalInternalMemorySize() {

        try {

            File path = Environment.getDataDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long totalBlocks = stat.getBlockCount();

            return String.valueOf(totalBlocks * blockSize);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";

    }

    /**
     * 存储卡是否存在
     *
     * @return
     */
    public static boolean externalMemoryAvailable() {
        try {
            return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        } catch (Exception e) {

        }
        return false;
    }

    /**
     * 获取可用的存储卡容量
     *
     * @return
     */
    public static String getAvailableExternalMemorySize() {
        try {
            if (externalMemoryAvailable()) {
                File path = Environment.getExternalStorageDirectory();
                StatFs stat = new StatFs(path.getPath());
                long blockSize = stat.getBlockSize();
                long availableBlocks = stat.getAvailableBlocks();

                return String.valueOf(availableBlocks * blockSize);

            } else {
                return "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取总的存储卡容量
     *
     * @return
     */
    public static String getTotalExternalMemorySize() {

        try {

            if (externalMemoryAvailable()) {
                File path = Environment.getExternalStorageDirectory();
                StatFs stat = new StatFs(path.getPath());
                long blockSize = stat.getBlockSize();
                long totalBlocks = stat.getBlockCount();


                return String.valueOf(totalBlocks * blockSize);

            } else {
                return "-1";
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "-1";
    }


    /**
     * 获取经纬度
     *
     * @param context
     * @return
     */
    public static String getLocation(Context context) {
        String result = "";
        try {
            Class cls = context.getClass();
            Object obj = cls.getMethod("getSystemService", String.class).invoke(context, "location");
            obj = obj.getClass().getMethod("getLastKnownLocation", String.class).invoke(obj, "network");
            if (obj != null) {
                result = obj.getClass().getMethod("getLatitude").invoke(obj).toString();
                result = result + "," + obj.getClass().getMethod("getLongitude").invoke(obj).toString();
            }

            //                    LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            //                    Location l = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            //                    if (l != null) {
            //                        result = l.getLatitude() + "," + l.getLongitude();
            //                    }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String getNetworkOperator(Context paramContext) {
        TelephonyManager localTelephonyManager = (TelephonyManager) paramContext.getSystemService(Context.TELEPHONY_SERVICE);

        return localTelephonyManager.getNetworkOperator();

    }


    /**
     * 移动国家码   区分国家
     *
     * @param context
     * @return
     */
    public static String getMcc(Context context) {
        //                        TelephonyManager falg = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        //                        String imsi = falg.getSubscriberId();
        //                        if (TextUtils.isEmpty(imsi) || "null".equals(imsi)) {
        //                            return "";
        //                        }
        //                        return imsi.substring(0, 3);

        String imsi = null;
        try {

            Class cls = context.getClass();
            Method method = cls.getMethod("getSystemService", new Class[]{String.class});
            Object obj = method.invoke(context, new Object[]{"phone"});

            method = obj.getClass().getMethod("getSubscriberId");
            imsi = (String) method.invoke(obj);

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (TextUtils.isEmpty(imsi) || "null".equals(imsi)) {
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
    public static String getMnc(Context context) {

        TelephonyManager mTelephonyMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        if (mTelephonyMgr.getPhoneType() != TelephonyManager.PHONE_TYPE_CDMA) {

            String mcc_mnc = mTelephonyMgr.getNetworkOperator();

            if (!TextUtils.isEmpty(mcc_mnc) && mcc_mnc.length() > 3) {

                return mcc_mnc.substring(3);
            }
        }
        String imsi = mTelephonyMgr.getSubscriberId();

        if (TextUtils.isEmpty(imsi) || imsi.length() < 6) {

            return "";
        }
        return imsi.substring(3, 5);
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