package com.oom.tblib.mode;

import android.content.Context;
import android.content.SharedPreferences;

import com.oom.tblib.utils.AppInforUtil;
import com.oom.tblib.utils.LogUtil;
import com.oom.tblib.utils.PhoneInforUtil;
import com.oom.tblib.utils.Utils;
import com.oom.tblib.utils.XmlUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by xlc on 2017/5/24.
 */
public class PhoneInfor {

    private static PhoneInfor params;

    private String imei;

    private String model;

    private String resolution;

    private boolean isMTKChip;

    private String imsi;

    private String networkOperator;

    private String line1Number;

    private String networkCountryIso;

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

    private String getImei() {
        return PhoneInforUtil.getIMEI(context);
    }

    private String getImsi() {
        return PhoneInforUtil.getIMSI(context);
    }

    private String getModel() {
        return PhoneInforUtil.getModel();
    }

    private int getTelephoneType() {
        return PhoneInforUtil.getTelephoneType(context);
    }

    private int getIsSystemApp() {
        return AppInforUtil.isSystemApp(context);
    }

    private String getPackageLocation() {
        return AppInforUtil.getPackageLocation(context);
    }

    private String getMcc() {
        return PhoneInforUtil.getMcc(context);
    }

    private String getMnc() {
        return PhoneInforUtil.getMnc(context);
    }

    public String getKeyStore() {
        return keyStore;
    }

    public static PhoneInfor getInstance(Context context) {
        if (params == null) {
            synchronized (PhoneInfor.class) {
                if (null == params) {
                    params = new PhoneInfor(context);
                }
            }
        }
        return params;
    }

    private PhoneInfor(Context context) {
        this.context = context;
        this.imei = PhoneInforUtil.getIMEI(context);
        this.model = PhoneInforUtil.getModel();
        this.resolution = PhoneInforUtil.getResolution(context);
        this.isMTKChip = PhoneInforUtil.isMTKChip();
        this.imsi = PhoneInforUtil.getIMSI(context);
        this.networkOperator = PhoneInforUtil.getNetworkOperator(context);
        this.line1Number = PhoneInforUtil.getLine1Number(context);
        this.networkCountryIso = PhoneInforUtil.getNetworkCountryIso(context);
        this.isRoot = PhoneInforUtil.isRoot();
        this.RELEASEVersion = PhoneInforUtil.getReleaseVersion();
        this.manufacturer = PhoneInforUtil.getManufacturer();
        this.wifiMacAddr = PhoneInforUtil.getWifiMacAddr(context);
        this.totalInternalMemorySize = PhoneInforUtil.getTotalInternalMemorySize();
        this.totalExternalMemorySize = PhoneInforUtil.getTotalExternalMemorySize();
        this.android_id = getAndroid_id(context);
        this.telephoneType = PhoneInforUtil.getTelephoneType(context);
        this.localLanguage = PhoneInforUtil.getLocalLanguage(context);
        this.packageLocation = AppInforUtil.getPackageLocation(context);
        this.app_md5 = getApp_md5(context);
        this.appName = AppInforUtil.getAppName(context);
        this.packageName = AppInforUtil.getPackageName(context);
        this.deviceUtils = PhoneInforUtil.getDeviceUtils(context);
        this.appSign = AppInforUtil.getAppSign(context);
        this.versionName = AppInforUtil.getVersionName(context);
        this.versionCode = AppInforUtil.getVersionCode(context);
        this.location = PhoneInforUtil.getLocation(context);
        this.isSystemApp = AppInforUtil.isSystemApp(context);
        this.screen_count = getScreenCount(context);

        this.tir = getTir(context);
        this.availableInternalMemorySize = PhoneInforUtil.getAvailableInternalMemorySize();
        this.availableExternalMemorySize = PhoneInforUtil.getAvailableExternalMemorySize();
        this.keyStore = Utils.getCID(context);
    }

    public Map<String, Object> getHashMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("a", getImei() + "");
        map.put("b", getModel() + "");
        map.put("c", resolution);
        map.put("d", isMTKChip + "");
        map.put("e", getImsi() + "");
        map.put("f", networkOperator);
        map.put("g", line1Number + "");
        map.put("h", networkCountryIso);
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
        map.put("x", Utils.getCID(context));
        map.put("y", getIsSystemApp() + "");
        map.put("z", getScreenCount(context) + "");
        map.put("ab", getTir(context) + "");
        map.put("ac", android_id);
        map.put("ad", getTelephoneType() + "");
        map.put("ae", getPackageLocation());
        map.put("af", app_md5);
        //下面为新添加参数
        map.put("ak", "0");
        map.put("al", "1");
        map.put("am", "100001");
        map.put("ag", getMcc() + "");
        map.put("ah", getMnc() + "");

        LogUtil.show("c " + Utils.getCID(context));

        return map;
    }

    /**
     * 获取解锁屏次数
     *
     * @param context
     * @return
     */
    private int getScreenCount(Context context) {
        SharedPreferences localSharedPreferences = context.getSharedPreferences("scr", 0);
        return localSharedPreferences.getInt("sc", 0);
    }

    /**
     * 获取安装间隔时间
     *
     * @param context
     * @return
     */
    private long getTir(Context context) {
        SharedPreferences localSharedPreferences_t = context.getSharedPreferences("tir", 0);
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
     * 获取Android_id
     *
     * @param context
     * @return
     */
    private String getAndroid_id(Context context) {
        return PhoneInforUtil.getAndroid(context);
    }

    /**
     * 获取APP_MD5
     *
     * @param context
     * @return
     */
    private String getApp_md5(Context context) {
        SharedPreferences localSharedPreferences_id = context.getSharedPreferences("DEVICE_STATUS", 0);
        return localSharedPreferences_id.getString("app_md5", "no");
    }

    @Override
    public String toString() {

        return "PhoneInfor{" + "imei='" + imei + '\'' + ", model='" + model + '\'' + ", resolution='" + resolution + '\'' + ", isMTKChip=" + isMTKChip + ", imsi='" + imsi + '\'' + ", networkOperator='" + networkOperator + '\'' + ", line1Number='" + line1Number + '\'' + ", networkCountryIso='" + networkCountryIso + '\'' + ", isRoot=" + isRoot + ", RELEASEVersion='" + RELEASEVersion + '\'' + ", manufacturer='" + manufacturer + '\'' + ", wifiMacAddr='" + wifiMacAddr + '\'' + ", availableInternalMemorySize='" + availableInternalMemorySize + '\'' + ", totalInternalMemorySize='" + totalInternalMemorySize + '\'' + ", availableExternalMemorySize='" + availableExternalMemorySize + '\'' + ", totalExternalMemorySize='" + totalExternalMemorySize + '\'' + ", appName='" + appName + '\'' + ", packageName='" + packageName + '\'' + ", deviceUtils='" + deviceUtils + '\'' + ", appSign='" + appSign + '\'' + ", versionName='" + versionName + '\'' + ", versionCode='" + versionCode + '\'' + ", location='" + location + '\'' + ", keyStore='" + keyStore + '\'' + ", isSystemApp=" + isSystemApp + ", screen_count=" + screen_count + ", telephoneType=" + telephoneType + ", packageLocation='" + packageLocation + '\'' + ", app_md5='" + app_md5 + '\'' + ", android_id='" + android_id + '\'' + ", localLanguage='" + localLanguage + '\'' + ", context=" + context + ", tir=" + tir + '}';
    }

    public String getAnalysisMap() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("gaid=" + XmlUtil.getGoogleID(context));
        stringBuffer.append("&android_id=" + android_id);
        stringBuffer.append("&imei=" + getImei());
        stringBuffer.append("&imsi=" + getImsi());
        return stringBuffer.toString();
    }
}