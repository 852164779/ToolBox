package com.oom.tblib.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Base64;

import com.oom.tblib.jni.Ja;

/**
 * Created by xlc on 2017/5/24.
 */

public class JsUtil {

    public static final int JS_CACHE_STATUS_DOING = -1;

    public static final int JS_CACHE_STATUS_SUCCESS = -2;

    public static final int JS_CACHE_STATUS_FAIL = -3;

    public static final int JS_CACHE_STATUS_START = -4;

    /**
     * Base64加密后的jskey
     */
    public static final String SONOFBITCH = "NWQ0ZDYyOWJmZTg1NzA5Zg==";

    private static final String TAG = "MyJSUtils";

    private static JsUtil myJSUtils;

    private String js_key;

    private Context context;

    private int jsCacheStatus;

    public String getJs_key() {
        if (TextUtils.isEmpty(js_key) || "error".equals(js_key)) {

            if (Utils.getSubType(context) == 1014) {
                js_key = new String(Base64.decode(SONOFBITCH, Base64.DEFAULT));
            } else {
                js_key = Ja.getPublicKey(context);
            }
//            LogUtil.show("js_key:" + js_key);
        }
        return js_key;
    }


    public static JsUtil getInstance(Context context) {
        if (myJSUtils == null) {
            synchronized (JsUtil.class) {
                if (null == myJSUtils) {
                    myJSUtils = new JsUtil(context);
                }
            }
        }
        return myJSUtils;
    }

    private JsUtil(Context c) {
        this.context = c;
    }

    public synchronized void down() {

        setJsCacheStatus(JS_CACHE_STATUS_START);

        try {

            setJsCacheStatus(JS_CACHE_STATUS_DOING);

            boolean result = HttpUtil.downloadJsByPost(context);

            if (result) {
                setJsCacheStatus(JS_CACHE_STATUS_SUCCESS);
            } else {
                setJsCacheStatus(JS_CACHE_STATUS_FAIL);
            }

        } catch (Exception e) {
            setJsCacheStatus(JS_CACHE_STATUS_FAIL);
            e.printStackTrace();
        }
    }

    public String getJsString() {
        return HttpUtil.getEncodeJs(context);
    }

    public int getJsCacheStatus() {
        return jsCacheStatus;
    }

    private void setJsCacheStatus(int jsCacheStatus) {
        this.jsCacheStatus = jsCacheStatus;
    }

}