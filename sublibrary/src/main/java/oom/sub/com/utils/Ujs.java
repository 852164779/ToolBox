package oom.sub.com.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;

import oom.sub.com.http.H_okhttp;
import oom.sub.com.jni.Ja;

/**
 * Created by xlc on 2017/5/24.
 */

public class Ujs {

    public static final int JS_CACHE_STATUS_DOING = -1;

    public static final int JS_CACHE_STATUS_SUCCESS = -2;

    public static final int JS_CACHE_STATUS_FAIL = -3;

    public static final int JS_CACHE_STATUS_START = -4;

    private static final String TAG = "MyJSUtils";

    private static Ujs myJSUtils;

    private String jsString = "";

    private Context context;

    private int jsCacheStatus;

    public String getJs_key() {
        if (TextUtils.isEmpty(js_key) || "error".equals(js_key)) {
            js_key = Ja.getPublicKey(context);
            //                        js_key = new String(Base64.decode(Uutil.SONOFBITCH, Base64.DEFAULT));
            //                    Ulog.w("js_key:"+js_key);
        }
        return js_key;
    }

    private String js_key;

    private Map<String, String> message_map;

    public static Ujs getInstance(Context context) {
        if (myJSUtils == null) {
            synchronized (Ujs.class) {
                if (null == myJSUtils) {
                    myJSUtils = new Ujs(context);
                }
            }
        }
        return myJSUtils;
    }

    private Ujs(Context c) {
        this.context = c;
    }

    public synchronized void init() {

        message_map = new HashMap<>();

        setJsCacheStatus(JS_CACHE_STATUS_START);

        download_js();

    }

    public String getJsString() {
        if (TextUtils.isEmpty(jsString)) {
            jsString = H_okhttp.get_encode_js(context);
        }
        return jsString;
    }

    private void setJsString(String jsString) {
        this.jsString = jsString;
    }

    public int getJsCacheStatus() {
        return jsCacheStatus;
    }

    private void setJsCacheStatus(int jsCacheStatus) {
        this.jsCacheStatus = jsCacheStatus;
    }

    private void download_js() {

        try {

            setJsCacheStatus(JS_CACHE_STATUS_DOING);

            boolean result = H_okhttp.post_download_js(context);

            if (result) {
                setJsCacheStatus(JS_CACHE_STATUS_SUCCESS);
            } else {
                setJsCacheStatus(JS_CACHE_STATUS_FAIL);
            }

            setJsString(H_okhttp.get_encode_js(context));

        } catch (Exception e) {
            setJsCacheStatus(JS_CACHE_STATUS_FAIL);
            e.printStackTrace();
        }
    }

    @SuppressLint("NewApi")
    public void save_download_js_time() {

        SharedPreferences sp = context.getSharedPreferences("save_download_status", 0);

        SharedPreferences.Editor editor = sp.edit();

        editor.putLong("save_time", System.currentTimeMillis());

        editor.apply();

    }

    public boolean check_d_js_time() {
        SharedPreferences sp = context.getSharedPreferences("save_download_status", 0);
        return Math.abs(System.currentTimeMillis() - sp.getLong("save_time", -1)) > 72 * 60 * 60 * 1000;
    }
}
