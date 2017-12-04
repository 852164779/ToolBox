package com.global.toolbox.util;

import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Environment;
import android.os.StatFs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Created by Admin on 2017/6/17.
 */

public class Usys {

    public static final String SYSINFOR_ACTION = "com.global.system.infor";
    public static final String NAME_PREFERENCES_APPINFOR = "_AppInfor";

    private static final Set<String> RTL;
    private static boolean cache = false;


    static {
        Set<String> lang = new HashSet<String>();
        lang.add("ar");
        lang.add("dv");
        lang.add("fa");
        lang.add("ha");
        lang.add("he");
        lang.add("iw");
        lang.add("ji");
        lang.add("ps");
        lang.add("ur");
        lang.add("yi");
        RTL = Collections.unmodifiableSet(lang);
    }

    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;

    }


    /**
     * 判断当前手机语言是否是RTL
     */
    public static boolean isTextRTL(Locale locale) {
        return RTL.contains(locale.getLanguage());
    }

    /**
     * RAM总大小
     */
    public static long getTotalRAMSize() {
        String str1 = "/proc/meminfo";
        String str2 = "";
        String[] arrayOfString;
        long totalSize = 0;

        try {
            FileReader localFileReader = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(localFileReader, 8192);

            String str3;
            while ((str3 = localBufferedReader.readLine()) != null) {
                if (str3.contains("MemTotal")) {
                    str2 = str3;
                }
            }

            arrayOfString = str2.split("\\s+");
            totalSize = Long.valueOf(arrayOfString[1]).longValue() * 1024;

            localBufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return totalSize;
    }

    /**
     * RAM可用大小
     */
    public static long getAvailRAMSize(Context mContext) {
        ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
        return getTotalRAMSize() - mi.availMem;
    }

    /**
     * ROM总大小
     */
    public static long getTotalROMSize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return totalBlocks * blockSize;  // Byte
    }

    /**
     * ROM可用空间
     */
    public static long getAvailROMSize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return getTotalROMSize() - availableBlocks * blockSize; // Byte
    }

    /**
     * Byte转KB、MG、GB
     *
     * @param falg true 强制转GB
     */
    public static String formatSize(long size, boolean falg) {
        String suffix = "B";// Byte
        String encode = "#0";
        float fSize = 0;

        size = Math.abs(size);

        if (size >= 1024) {
            suffix = "KB";
            fSize = size / 1024;

            if (fSize >= 1024) {
                suffix = "MB";
                fSize /= 1024;
            }
            if (fSize >= 1024) {
                suffix = "GB";
                fSize /= 1024;
                encode = "#0.00";
            }
        } else {
            fSize = size;
        }

        if (falg && suffix.equals("MB")) {
            fSize /= 1024;
            suffix = "GB";
            encode = "#0.00";
        }

        //        Locale.setDefault(Locale.CHINESE);//切换任何语言都显示阿拉伯数字
        java.text.DecimalFormat df = new java.text.DecimalFormat(encode);
        StringBuilder resultBuffer = new StringBuilder(df.format(fSize));

        if (suffix != null) {
            if (Usys.isTextRTL(Locale.getDefault()) && cache) {
                String test = "";
                for (int i = 0; i < suffix.length(); i++) {
                    test = test + Usys.decodeUnicode("\\u200F") + suffix.substring(i, i + 1);
                }
                suffix = test;
                cache = false;
            }
            resultBuffer.append(suffix);
        }

        return resultBuffer.toString();
    }


    public static float formatSizeFloat(long size, boolean falg) {
        String suffix = "B";// Byte
        float fSize = 0;

        size = Math.abs(size);

        if (size >= 1024) {
            suffix = "KB";
            fSize = size / 1024;
            if (fSize >= 1024) {
                suffix = "MB";
                fSize /= 1024;
            }
            if (fSize >= 1024) {
                suffix = "GB";
                fSize /= 1024;
            }
        } else {
            fSize = size;
        }

        if (falg && suffix.equals("MB")) {
            fSize /= 1024;
        }
        return fSize;
    }

    public static void saveSharedInfor(Context context, Map<String, Long> data) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Usys.NAME_PREFERENCES_APPINFOR, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        for (Map.Entry<String, Long> entry : data.entrySet()) {
            editor.putLong(entry.getKey(), entry.getValue());
        }
        editor.commit();
    }

    /**
     * 获取持久化数据
     *
     * @param strName 键
     */
    public static String getSharePreferenceStr(Context context, String strName) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(NAME_PREFERENCES_APPINFOR, Context.MODE_PRIVATE);
        return sharedPreferences.getString(strName, "0");
    }

    public static long getTotalCache(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(NAME_PREFERENCES_APPINFOR, Context.MODE_PRIVATE);
        return sharedPreferences.getLong("cache", 0);
    }

    /**
     * 获取持久化数据
     *
     * @param strName 键
     */
    public static long getSharePreferenceLong(Context context, String strName) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(NAME_PREFERENCES_APPINFOR, Context.MODE_PRIVATE);
        return sharedPreferences.getLong(strName, 0);
    }

    /**
     * 保存总清理垃圾数目
     *
     * @param value 值
     */
    public static void saveTotalCache(Context context, long value) {
        SharedPreferences sp = context.getSharedPreferences(NAME_PREFERENCES_APPINFOR, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putLong("cache", value);
        editor.apply();
    }

    /**
     * Unicode 转 String
     */
    public static String decodeUnicode(final String dataStr) {
        int start = 0;
        int end = 0;
        final StringBuffer buffer = new StringBuffer();
        while (start > -1) {
            end = dataStr.indexOf("\\u", start + 2);
            String charStr = "";
            if (end == -1) {
                charStr = dataStr.substring(start + 2, dataStr.length());
            } else {
                charStr = dataStr.substring(start + 2, end);
            }
            char letter = (char) Integer.parseInt(charStr, 16); // 16进制parse整形字符串。
            buffer.append(new Character(letter).toString());
            start = end;
        }
        return buffer.toString();
    }

    public static String getTotalCache(long size) {
        cache = true;
        if (Usys.formatSizeFloat(size, true) < 0.9) {
            return Usys.formatSize(size, false);
        }
        return Usys.formatSize(size, true);
    }


    public static boolean checkArrkiiInitTime(Context context) {
        return Math.abs(System.currentTimeMillis() - getSharePreferenceLong(context, "ArrKii_Time")) > 1000 * 60 * 30;
    }
}