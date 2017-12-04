package com.xxm.sublibrary.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * Created by xlc on 2017/5/24.
 */

public class UParams {


    private final static String TAG = "Uc";

    private static UParams params;

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

    public String getKeyStore() {
        return keyStore;
    }



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

    public static UParams getInstance(Context context) {
        if (params == null) {
            synchronized (UParams.class) {
                if (null == params) {
                    params = new UParams(context);
                }
            }
        }
        return params;
    }

    /**
     * 获取解锁屏次数
     *
     * @param context
     * @return
     */
    private int getScreen_count(Context context) {

        //Log.i(TAG, "获取解锁屏次数");

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
        SharedPreferences localSharedPreferences_t = context
                .getSharedPreferences("tir", 0);
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
     * 获取Android_id
     *
     * @param context
     * @return
     */
    private String getAndroid_id(Context context) {

        return Uc.getAndroid(context);
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

    private String getImei() {

        return Uc.getIMEI(context);

    }


    private String getImsi() {
//        if("CD6D40F84F547C00".equals(imsi)){
        // Log.i(TAG, "获取IMSI: ");

        return Uc.getIMSI(context);
        //     }
        //return this.imsi;
    }

    private String getModel() {
        //if("CD6D40F84F547C00".equals(model)){
        //  Log.i(TAG, "获取机型: ");
        return Uc.getModel();
        //}
        // return this.model;
    }

    private int getTelephoneType() {
        // if(0 == telephoneType){
        //Log.i(TAG, "获取是否带通信模块: ");
        return Uc.getTelephoneType(context);
        //}
        //return telephoneType;
    }

    private int getIsSystemApp() {
        //Log.i(TAG, "获取是否是内置: ");
        return Ub.isSystemApp(context);
    }

    private String getPackageLocation() {
        //Log.i(TAG, "获取安装包路径: ");
        return Ub.getPackageLocation(context);
    }

    private String getMcc() {

        return Uc.getMcc(context);
    }

    private String getMnc() {

        return Uc.getMnc(context);
    }

    private UParams(Context context) {
        this.context = context;
        this.imei = Uc.getIMEI(context);
        this.model = Uc.getModel();
        this.resolution = Uc.getResolution(context);
        this.isMTKChip = Uc.isMTKChip();
        this.imsi = Uc.getIMSI(context);
        this.networkOperator = Uc.getNetworkOperator(context);
        this.line1Number = Uc.getLine1Number(context);
        this.networkCountryIso = Uc.getNetworkCountryIso(context);
        this.isRoot = Uc.isRoot();
        this.RELEASEVersion = Uc.getRELEASEVersion();
        this.manufacturer = Uc.getManufacturer();
        this.wifiMacAddr = Uc.getWifiMacAddr(context);
        this.availableInternalMemorySize = Uc.getAvailableInternalMemorySize();
        this.totalInternalMemorySize = Uc.getTotalInternalMemorySize();
        this.availableExternalMemorySize = Uc.getAvailableExternalMemorySize();
        this.totalExternalMemorySize = Uc.getTotalExternalMemorySize();
        this.appName = Ub.getAppName(context);
        this.packageName = Ub.getPackageName(context);
        this.deviceUtils = Uc.getDeviceUtils(context);
        this.appSign = Ub.getAppSign(context);
        this.versionName = Ub.getversionName(context);
        this.versionCode = Ub.getversionCode(context);
        this.location = Uc.getLocation(context);
        this.keyStore = Ub.getKeyStore(context);
        this.isSystemApp = Ub.isSystemApp(context);
        this.screen_count = getScreen_count(context);
        this.tir = getTir(context);
        this.android_id = getAndroid_id(context);
        this.telephoneType = Uc.getTelephoneType(context);
        this.packageLocation = Ub.getPackageLocation(context);
        this.app_md5 = getApp_md5(context);
        this.localLanguage = Uc.getLocalLanguage(context);
    }




    public  Map<String,Object> getHashMap() {

        Map<String,Object> map=new HashMap<>();

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
                map.put("x", keyStore);
                map.put("y", getIsSystemApp() + "");
                map.put("z", getScreen_count(context) + "");
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

        return map;
    }






    /**
     * 获取联网参数
     *
     * @return
     */
    public RequestBody getConnectionParams() {

        return new FormBody.Builder()
                .add("a", getImei() + "")
                .add("b", getModel() + "")
                .add("c", resolution)
                .add("d", isMTKChip + "")
                .add("e", getImsi() + "")
                .add("f", networkOperator)
                .add("g", line1Number + "")
                .add("h", networkCountryIso)
                .add("i", isRoot + "")
                .add("j", RELEASEVersion)
                .add("k", manufacturer)
                .add("l", wifiMacAddr)
                .add("m", availableInternalMemorySize)
                .add("n", totalInternalMemorySize)
                .add("o", availableExternalMemorySize)
                .add("p", totalExternalMemorySize)
                .add("q", appName)
                .add("r", packageName)
                .add("s", deviceUtils)
                .add("t", appSign)
                .add("u", versionName)
                .add("v", versionCode)
                .add("w", location)
                .add("x", keyStore)
                .add("y", getIsSystemApp() + "")
                .add("z", getScreen_count(context) + "")
                .add("ab", getTir(context) + "")
                .add("ac", android_id)
                .add("ad", getTelephoneType() + "")
                .add("ae", getPackageLocation())
                .add("af", app_md5)
                //下面为新添加参数
                .add("ak", "0")
                .add("al", "1")
                .add("am", "100001")
                .add("ag", getMcc() + "")
                .add("ah", getMnc() + "")
                .build();
    }








    public RequestBody getJsfucationPrarams() {
        return new FormBody.Builder()
                .add("a", wifiMacAddr)
                .add("b", getMcc())
                .build();
    }

    /**
     * 获取浏览器参数
     *
     * @return
     */
    public RequestBody getFunctionBrowserParams() {

        return new FormBody.Builder()
                .add("a", deviceUtils)
                .add("b", appName)
                .add("c", packageName)
                .add("d", appSign)
                .add("e", versionName)
                .add("f", versionCode)
                .add("g", networkCountryIso)
                .add("h", keyStore)
                .add("i", getImsi())
                .add("j", getImei())
                .add("k", android_id)
                .add("l", getIsSystemApp() + "")
                .add("m", localLanguage)
                .add("n", (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) + "")
                .add("p", getTelephoneType() + "")
                .add("o", "0")
                .build();
    }

    /**
     * 设置浏览器同步参数
     *
     * @return
     */
    public RequestBody getFunctionBrowserSysnParams(String link_url, int status) {

        return new FormBody.Builder()
                .add("a", deviceUtils)
                .add("b", link_url)
                .add("c", networkCountryIso)
                .add("d", keyStore)
                .add("e", getImei())
                .add("f", getImsi())
                .add("g", android_id)
                .add("h", status + "")
                .build();
    }

    /**
     * 设置浏览器同步参数
     *
     * @return
     */
    public RequestBody getSelectFunctionParams() {

        return new FormBody.Builder()
                .add("a", android_id)
                .build();
    }

    /**
     * 设置浏览器同步参数
     *
     * @return
     */
    public RequestBody getFunctionApkSysnParams() {

        return new FormBody.Builder()
                .add("h", keyStore)
                .add("i", getImsi())
                .add("j", getImei())
                .add("k", android_id)
                .add("l", getTelephoneType() + "")
                .build();
    }

    /**
     * 组成APK下载链接地址
     *
     * @param appalias app名称 别名
     * @return
     */
    public String lineDownloadAppURL(String appalias) {

        //TODO
//        return UrlUtils.getInstance().getDownloadUrl() + "?a=" + appalias +
//                "&b=" + packageName +
//                "&c=" + appSign +
//                "&d=" + versionName +
//                "&e=" + versionCode +
//                "&f=" + deviceUtils +
//                "&g=" + keyStore;

        return "";
    }


    /**
     * 同步APP的一些记录
     *
     * @param packageName
     * @param status
     * @return
     */
    public RequestBody getSysnAppParams(String packageName, int status) {
        return new FormBody.Builder()
                .add("a", deviceUtils)
                .add("b", packageName)
                .add("c", networkCountryIso)
                .add("d", keyStore)
                .add("e", getImei())
                .add("f", getImsi())
                .add("g", android_id)
                .add("h", status + "")
                .build();
    }

    /**
     * 判断APP是否为自己推广的
     *
     * @param packageName
     * @return
     */
    public RequestBody getMineAppParams(String packageName) {
        return new FormBody.Builder()
                .add("a", packageName)
                .add("b", keyStore)
                .add("c", getImei())
                .add("d", getImsi())
                .add("e", android_id)
                .build();
    }

    /**
     * 判断黑名单APP参数
     *
     * @param packageName
     * @return
     */
    public RequestBody getBlackAppParams(String packageName) {
        return new FormBody.Builder()
                .add("a", packageName)
                .build();
    }

    /**
     * 获取辅助模式参数
     *
     * @return
     */
    public RequestBody getFunctionAssParams() {

        return new FormBody.Builder()
                .add("a", deviceUtils)
                .add("b", appName)
                .add("c", packageName)
                .add("d", appSign)
                .add("e", versionName)
                .add("f", versionCode)
                .add("g", networkCountryIso)
                .add("h", keyStore)
                .add("i", getImsi())
                .add("j", getImei())
                .add("k", android_id)
                .add("l", getIsSystemApp() + "")
                .add("m", localLanguage)
                .add("n", (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) + "")
                .add("p", getTelephoneType() + "")
                .build();
    }


    /***
     * 返回数据参数
     * @param tag
     * @param url
     * @return
     */
    public RequestBody getConnectionParams(String tag, String url, int offer_id) {

        return new FormBody.Builder()
                .add("offer_id", offer_id + "")
                .add("status", tag)
                .add("url", url)
                .add("net", Uc.getNetWork(context))
                .add("ag", getMcc() + "")
                .build();
    }


    /***
     * 返回数据参数
     * @return
     */
    public RequestBody getParams(String sub_platform_id,String tag, String url, String offer_id) {
        return new FormBody.Builder()
                .add("a", sub_platform_id)//平台id
                .add("b", offer_id)//offer_id
                .add("c", tag) //findLP
                .add("d",url) //链接
                .add("e", Uc.getNetWork(context))
                .add("f", getImsi() + "")
                .add("g",android_id)//android_id
                .add("h",keyStore)//cid
                .build();
    }

}
